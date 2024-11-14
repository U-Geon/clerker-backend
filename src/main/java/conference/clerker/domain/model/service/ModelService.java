package conference.clerker.domain.model.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sagemakerruntime.AmazonSageMakerRuntime;
import com.amazonaws.services.sagemakerruntime.AmazonSageMakerRuntimeClientBuilder;
import com.amazonaws.services.sagemakerruntime.model.InvokeEndpointRequest;
import com.amazonaws.services.sagemakerruntime.model.InvokeEndpointResult;
import com.amazonaws.util.IOUtils;
import conference.clerker.domain.meeting.schema.FileType;
import conference.clerker.domain.meeting.service.MeetingFileService;
import conference.clerker.domain.meeting.service.MeetingService;
import conference.clerker.domain.model.dto.request.ModelRequestDTO;
import conference.clerker.domain.model.dto.response.ModelResponseDTO;
import conference.clerker.global.aws.s3.S3FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ModelService {

    private final MeetingFileService meetingFileService;
    private final S3FileService s3FileService;
    private final MeetingService meetingService;
    private final AsyncModelService asyncModelService;

    // AWS 자격 증명을 설정하기 위해 필요한 값 주입
    @Value("${other.aws.access-key}")
    private String accessKey;

    @Value("${other.aws.secret-key}")
    private String secretKey;

    @Value("${other.aws.sagemaker.endpoint}")
    private String endpointName;

    @Value("${other.aws.s3.bucket}")
    private String modelServerBucketName;

    // AWS SDK의 SageMakerRuntime 클라이언트
    private AmazonSageMakerRuntime sagemakerRuntimeClient;

    // 초기화 블록 또는 생성자에서 AWS 클라이언트 설정
    @PostConstruct
    private void init() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        this.sagemakerRuntimeClient = AmazonSageMakerRuntimeClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion("us-east-1") // 실제 AWS 리전으로 변경하세요
                .build();
    }

    @Transactional
    public ResponseEntity<String> sendToModelServer(String domain, MultipartFile webmFile, Long meetingId) {
        try {
            MultipartFile mp3File = convertWebmToMp3(webmFile);
            String mp3FileUrl = s3FileService.uploadFile("mp3File", mp3File.getOriginalFilename(), mp3File);
            log.info("mp3FileUrl: {}", mp3FileUrl);

            ModelRequestDTO modelRequestDTO = new ModelRequestDTO(domain, mp3FileUrl);
            log.info("modelRequestDTO: {}", modelRequestDTO);

            // AsyncModelService에서 비동기 요청 실행
            asyncModelService.invokeModelServerAsync(modelRequestDTO, mp3File, meetingId, domain);

            return ResponseEntity.accepted().body("추론 요청이 접수되었습니다.");

        } catch (IOException e) {
            log.error("파일 변환 중 IO 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("파일 변환 중 IO 에러 발생: " + e.getMessage());
        } catch (Exception e) {
            log.error("예기치 않은 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("예기치 않은 에러 발생: " + e.getMessage());
        }
    }

    @Async
    @Transactional
    public void invokeModelServerAsync(ModelRequestDTO modelRequestDTO, MultipartFile mp3File, Long meetingId, String domain) {
        log.info("invokeModelServerAsync 호출: meetingId={}, domain={}", meetingId, domain);
        try {
            invokeModelServer(modelRequestDTO, meetingId, domain);
        } catch (Exception e) {
            log.error("모델 서버 호출 중 오류 발생: {}", e.getMessage(), e);
        } finally {
            closeMp3File(mp3File);
        }
    }


    private void invokeModelServer(ModelRequestDTO modelRequestDTO, Long meetingId, String domain) throws IOException {
        log.info("invokeModelServer 호출: meetingId={}, domain={}", meetingId, domain);

        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(modelRequestDTO);

        // SageMaker 엔드포인트 호출 요청 생성
        InvokeEndpointRequest invokeEndpointRequest = new InvokeEndpointRequest()
                .withEndpointName(endpointName)
                .withContentType("application/json")
                .withBody(ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8)));

        // SageMaker 엔드포인트 호출
        InvokeEndpointResult invokeEndpointResult = sagemakerRuntimeClient.invokeEndpoint(invokeEndpointRequest);

        // 응답을 문자열로 변환
        String responseBody = StandardCharsets.UTF_8.decode(invokeEndpointResult.getBody()).toString();

        // 응답 JSON을 ModelResponseDTO로 변환
        ModelResponseDTO responseDTO = objectMapper.readValue(responseBody, ModelResponseDTO.class);

        // 받은 ModelResponseDTO를 통한 로직 실행
        processModelResponse(responseDTO, meetingId, domain);
    }

    //테스트용
    @Transactional
    public void testProcessModelResponse(ModelResponseDTO response, Long meetingId, String domain) {
        meetingService.endMeeting(meetingId, domain);
        processModelResponse(response, meetingId, domain);
    }

    // 받은 ModelResponseDTO를 통한 로직 실행.
    public void processModelResponse(ModelResponseDTO response, Long meetingId, String domain) {
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

            // 보고서
            String reportUrl = s3FileService.processAndUploadMarkdownFile(modelServerBucketName, response.report(),
                    "report", meetingId, imageUrlMap);
            String reportName = meetingId + "_" + response.report().substring(response.report().lastIndexOf("/") + 1);
            meetingFileService.create(meetingId, FileType.REPORT, reportUrl, reportName);

            // 원문
            String sttUrl = s3FileService.transferFileFromOtherS3(modelServerBucketName, response.stt(),
                    "stt", meetingId);
            String sttFileName = meetingId + "_" + response.stt().substring(response.stt().lastIndexOf("/") + 1);
            meetingFileService.create(meetingId, FileType.STT_RAW, sttUrl, sttFileName);

        } catch (Exception e) {
            log.error("파일 다운 중 에러 발생: {}", e.getMessage());
        }
    }

    private MultipartFile convertWebmToMp3(MultipartFile webmFile) {
        File tempWebmFile = null;
        File mp3File = null;

        try {
            tempWebmFile = File.createTempFile("temp", ".webm");
            webmFile.transferTo(tempWebmFile);

            mp3File = new File(tempWebmFile.getAbsolutePath().replace(".webm", ".mp3"));

            // ffmpeg 실행 경로 설정 필요
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "/opt/homebrew/bin/ffmpeg", // 실제 ffmpeg 경로로 변경 필요
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

    private void cleanUpTempFiles(File tempWebmFile, File mp3File) {
        if (tempWebmFile != null && tempWebmFile.exists() && !tempWebmFile.delete()) {
            log.error("임시 webm 파일 삭제 실패: {}", tempWebmFile.getAbsolutePath());
        }
        if (mp3File != null && mp3File.exists() && !mp3File.delete()) {
            log.error("변환된 MP3 파일 삭제 실패: {}", mp3File.getAbsolutePath());
        }
    }

    public void closeMp3File(MultipartFile mp3File) {
        try {
            if (mp3File != null) {
                mp3File.getInputStream().close();
            }
        } catch (IOException e) {
            log.error("MP3 파일 스트림 닫기 실패: {}", e.getMessage());
        }
    }
}