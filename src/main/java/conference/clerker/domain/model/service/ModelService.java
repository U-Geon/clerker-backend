package conference.clerker.domain.model.service;

import com.amazonaws.util.IOUtils;
import conference.clerker.domain.model.dto.request.ModelRequestDTO;
import conference.clerker.domain.model.dto.response.ModelResponseDTO;
import conference.clerker.global.aws.s3.S3FileService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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

    @Value("${baseUrl.model}")
    private String baseUrl;

    private final WebClient.Builder webClientBuilder;
    private final S3FileService s3FileService;

    @Transactional
    public Mono<ModelResponseDTO> sendToModelServer(List<String> keywords, MultipartFile webmFile) {
        try {
            // 1. webm 파일을 mp3로 변환
            MultipartFile mp3File = convertWebmToMp3(webmFile);

            // 2. 변환된 mp3 파일을 S3에 업로드
            String mp3FileUrl = s3FileService.uploadFile("mp3File", mp3File.getOriginalFilename(), mp3File);

            // 3. 모델 서버에 전송할 DTO 생성
            WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();
            ModelRequestDTO modelRequestDTO = new ModelRequestDTO(keywords, mp3FileUrl);

            // 4. WebClient를 사용하여 모델 서버에 요청 보내기
            return webClient.post()
                    .uri("/model_serve")
                    .body(BodyInserters.fromValue(modelRequestDTO))
                    .retrieve()
                    .bodyToMono(ModelResponseDTO.class)
                    .doFinally(signalType -> closeMp3File(mp3File));
        } catch (IOException e) {
            return Mono.error(new IllegalStateException("파일 변환 중 IO 에러 발생: " + e.getMessage(), e));
        } catch (InterruptedException e) {
            return Mono.error(new IllegalStateException("파일 변환 중 인터럽트 발생: " + e.getMessage(), e));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public String test1(MultipartFile webmFile) {
        // 1. MultipartFile이 비어 있는지 확인
        if (webmFile == null || webmFile.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 없습니다.");
        }

        try {
            MultipartFile multipartFile = convertWebmToMp3(webmFile);
            return s3FileService.uploadFile("mp3File", multipartFile.getOriginalFilename(), multipartFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 중 IO 에러 발생: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new RuntimeException("파일 변환 중 인터럽트 발생: " + e.getMessage(), e);
        }
    }

    private MultipartFile convertWebmToMp3(MultipartFile webmFile) throws IOException, InterruptedException {
        File tempWebmFile = null;
        File mp3File = null;

        try {
            // 임시 .webm 파일 생성
            tempWebmFile = File.createTempFile("temp", ".webm");
            webmFile.transferTo(tempWebmFile);

            // 변환될 MP3 파일 경로 설정
            mp3File = new File(tempWebmFile.getAbsolutePath().replace(".webm", ".mp3"));

            // FFmpeg 명령어 실행
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "/opt/homebrew/bin/ffmpeg",
                    "-i", tempWebmFile.getAbsolutePath(),
                    "-codec:a", "libmp3lame",
                    "-b:a", "128k",
                    "-ar", "44100",
                    mp3File.getAbsolutePath()
            );
            Process process = processBuilder.start();

            // FFmpeg 프로세스 완료 대기
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg 실행 실패: exit code " + exitCode);
            }

            // MP3 파일을 바이트 배열로 읽어 MultipartFile 생성
            byte[] mp3Bytes = IOUtils.toByteArray(new FileInputStream(mp3File));
            return createMultipartFile(mp3File, mp3Bytes);
        } finally {
            // 임시 파일 삭제
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
            public InputStream getInputStream() throws IOException {
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

    private void closeMp3File(MultipartFile mp3File) {
        try {
            if (mp3File != null && mp3File.getInputStream() != null) {
                mp3File.getInputStream().close();
            }
        } catch (IOException e) {
            log.error("MP3 파일 스트림 닫기 실패: {}", e.getMessage());
        }
    }
}
