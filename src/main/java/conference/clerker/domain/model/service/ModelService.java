package conference.clerker.domain.model.service;

import com.amazonaws.util.IOUtils;
import conference.clerker.domain.meeting.schema.FileType;
import conference.clerker.domain.meeting.service.MeetingFileService;
import conference.clerker.domain.meeting.service.MeetingService;
import conference.clerker.domain.model.dto.request.ModelRequestDTO;
import conference.clerker.domain.model.dto.response.ModelResponseDTO;
import conference.clerker.global.aws.s3.S3FileService;
import java.io.*;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ModelService {

    private final MeetingFileService meetingFileService;
    @Value("${baseUrl.model}")
    private String baseUrl;

    @Value("${other.aws.s3.bucket}")
    private String modelServerBucketName;

    private final WebClient.Builder webClientBuilder;
    private final S3FileService s3FileService;
    private final MeetingService meetingService;

    @Transactional
    public Mono<ModelResponseDTO> sendToModelServer(String domain, MultipartFile webmFile, Long meetingId) {
        try {
            // 1. webm 파일을 mp3로 변환
            MultipartFile mp3File = convertWebmToMp3(webmFile);

            // 2. 변환된 mp3 파일을 S3에 업로드
            String mp3FileUrl = s3FileService.uploadFile("mp3File", mp3File.getOriginalFilename(), mp3File);

            log.info("mp3FileUrl: {}", mp3FileUrl);

            // 3. 모델 서버에 전송할 DTO 생성
            WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();
            ModelRequestDTO modelRequestDTO = new ModelRequestDTO(domain, mp3FileUrl);

            log.info("modelRequestDTO: {}", modelRequestDTO);

            // 4. WebClient를 사용하여 모델 서버에 요청 보내기
            return webClient.post()
                    .uri("/endpoints/clerker-ai/invocations")
                    .header("Content-Type", "application/json") // SageMaker 엔드포인트가 JSON을 처리한다고 가정
                    .body(BodyInserters.fromValue(modelRequestDTO))
                    .retrieve()
                    .bodyToMono(ModelResponseDTO.class)
                    .doOnNext(response -> processModelResponse(response, meetingId, domain)) // 받은 ModelResponseDTO를 통한 로직 실행.
                    .doFinally(signalType -> closeMp3File(mp3File));
        } catch (IOException e) {
            log.error("파일 변환 중 IO 에러 발생: {}", e.getMessage());
            return Mono.error(new IllegalStateException("파일 변환 중 IO 에러 발생: " + e.getMessage(), e));
        } catch (Exception e) {
            log.error("예기치 않은 에러 발생: {}", e.getMessage());
            return Mono.error(new IllegalStateException("예기치 않은 에러 발생: " + e.getMessage(), e));
        }
    }

    //테스트용
    @Transactional
    public void testProcessModelResponse(ModelResponseDTO response, Long meetingId, String domain) {
        meetingService.endMeeting(meetingId, domain);
        processModelResponse(response, meetingId, domain);
    }

    // 받은 ModelResponseDTO를 통한 로직 실행.
    private void processModelResponse(ModelResponseDTO response, Long meetingId, String domain) {
        log.info("processModelResponse 실행");

        // meeting 엔티티 컬럼 변경 (회의 종료)
        meetingService.endMeeting(meetingId, domain);

        // 여기서 받은 url들을 토대로 파일을 s3에 저장한 뒤 DB에 버킷 경로 저장
        try {
            // zip 파일 압축 해제 후 이미지 업로드
            Map<String, String> imageUrlMap = s3FileService.transferZipContentFromOtherS3(modelServerBucketName, response.diagram_image(), "images", meetingId);
            // 업로드된 이미지의 파일명과 URL을 MeetingFile에 저장
            for (Map.Entry<String, String> entry : imageUrlMap.entrySet()) {
                String fileName = entry.getKey();      // 이미지 파일명
                String imageUrl = entry.getValue();    // 업로드된 이미지 URL
                meetingFileService.create(meetingId, FileType.IMAGE, imageUrl, fileName);
            }

            //보고서
            String reportUrl = s3FileService.processAndUploadMarkdownFile(modelServerBucketName, response.report(),
                    "report", meetingId, imageUrlMap);
            String reportName = meetingId + "_" + response.report().substring(response.report().lastIndexOf("/") + 1);
            meetingFileService.create(meetingId, FileType.REPORT, reportUrl, reportName);

            //원문
            String sttUrl = s3FileService.transferFileFromOtherS3(modelServerBucketName, response.stt(),
                    "stt", meetingId);
            String sttFileName = meetingId + "_" + response.stt().substring(response.stt().lastIndexOf("/") + 1);
            meetingFileService.create(meetingId, FileType.STT_RAW, sttUrl, sttFileName);

        } catch (Exception e){
            log.error("파일 다운 중 에러 발생: {}", e.getMessage());
        }

    }


    // webm to mp3 이후 s3에 저장하는 로직 테스트
//    public String test1(MultipartFile webmFile) {
//        if (webmFile == null || webmFile.isEmpty()) {
//            log.error("업로드된 파일이 없습니다.");
//            throw new IllegalArgumentException("업로드된 파일이 없습니다.");
//        }
//
//        try {
//            MultipartFile mp3File = convertWebmToMp3(webmFile);
//            return s3FileService.uploadFile("mp3File", mp3File.getOriginalFilename(), mp3File);
//        } catch (IOException e) {
//            log.error("파일 변환 중 IO 에러 발생: {}", e.getMessage());
//            throw new RuntimeException("파일 변환 중 IO 에러 발생: " + e.getMessage(), e);
//        } catch (Exception e) {
//            log.error("예기치 않은 에러 발생: {}", e.getMessage());
//            throw new RuntimeException("예기치 않은 에러 발생: " + e.getMessage(), e);
//        }
//    }

    private MultipartFile convertWebmToMp3(MultipartFile webmFile) {
        File tempWebmFile = null;
        File mp3File = null;

        try {
            tempWebmFile = File.createTempFile("temp", ".webm");
            webmFile.transferTo(tempWebmFile);

            mp3File = new File(tempWebmFile.getAbsolutePath().replace(".webm", ".mp3"));

            // 추후에 해당 저장 경로를 인스턴스 내 ffmpeg 라이브러리 설치 경로로 변경
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "/opt/homebrew/bin/ffmpeg",
                    "-i", tempWebmFile.getAbsolutePath(),
                    "-codec:a", "libmp3lame",
                    "-b:a", "128k",
                    "-ar", "44100",
                    mp3File.getAbsolutePath()
            );
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("FFmpeg 실행 실패: exit code {}", exitCode);
                throw new IOException("FFmpeg 실행 실패: exit code " + exitCode);
            }

            byte[] mp3Bytes = IOUtils.toByteArray(new FileInputStream(mp3File));
            return createMultipartFile(mp3File, mp3Bytes);
        } catch (IOException | InterruptedException e) {
            log.error("파일 변환 실패: {}", e.getMessage());
            throw new RuntimeException("파일 변환 실패: " + e.getMessage(), e);
        } finally {
            cleanUpTempFiles(tempWebmFile, mp3File);
        }
    }

    // mp3File 타입을 File에서 MultipartFile로 변경하는 로직.
    private MultipartFile createMultipartFile(File mp3File, byte[] mp3Bytes) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return mp3File.getName();
            }

            @Override
            public String getOriginalFilename() {
                return mp3File.getName();
            }

            @Override
            public String getContentType() {
                return "audio/mp3";
            }

            @Override
            public boolean isEmpty() {
                return mp3Bytes.length == 0;
            }

            @Override
            public long getSize() {
                return mp3Bytes.length;
            }

            @Override
            public byte[] getBytes() {
                return mp3Bytes;
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(mp3Bytes);
            }

            @Override
            public void transferTo(File dest) throws IOException {
                try (FileOutputStream out = new FileOutputStream(dest)) {
                    out.write(mp3Bytes);
                }
            }
        };
    }

    // mp3로 변환하면서 생기는 임시 파일 삭제
    private void cleanUpTempFiles(File tempWebmFile, File mp3File) {
        if (tempWebmFile != null && tempWebmFile.exists() && !tempWebmFile.delete()) {
            log.error("임시 webm 파일 삭제 실패: {}", tempWebmFile.getAbsolutePath());
        }
        if (mp3File != null && mp3File.exists() && !mp3File.delete()) {
            log.error("변환된 MP3 파일 삭제 실패: {}", mp3File.getAbsolutePath());
        }
    }

    // s3 업로드 후 로컬 환경에 설치되는 mp3 파일 삭제
    private void closeMp3File(MultipartFile mp3File) {
        try {
            if (mp3File != null) {
                mp3File.getInputStream();
                mp3File.getInputStream().close();
            }
        } catch (IOException e) {
            log.error("MP3 파일 스트림 닫기 실패: {}", e.getMessage());
        }
    }
}
