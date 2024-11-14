package conference.clerker.domain.model.service;

import com.amazonaws.services.sagemakerruntime.AmazonSageMakerRuntime;
import com.amazonaws.services.sagemakerruntime.model.InvokeEndpointRequest;
import com.amazonaws.services.sagemakerruntime.model.InvokeEndpointResult;
import conference.clerker.domain.meeting.schema.Status;
import conference.clerker.domain.model.dto.request.ModelRequestDTO;
import conference.clerker.domain.model.dto.response.ModelResponseDTO;
import conference.clerker.global.aws.s3.S3FileService;
import conference.clerker.domain.meeting.schema.FileType;
import conference.clerker.domain.meeting.service.MeetingFileService;
import conference.clerker.domain.meeting.service.MeetingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncModelService {

    private final AmazonSageMakerRuntime sagemakerRuntimeClient;
    private final MeetingFileService meetingFileService;
    private final S3FileService s3FileService;
    private final MeetingService meetingService;

    @Value("${other.aws.sagemaker.endpoint}")
    private String endpointName;

    @Value("${other.aws.s3.bucket}")
    private String modelServerBucketName;

    @Async
    @Transactional
    public void invokeModelServerAsync(ModelRequestDTO modelRequestDTO, MultipartFile mp3File, Long meetingId, String domain) {
        log.info("invokeModelServerAsync 호출: meetingId={}, domain={}", meetingId, domain);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(modelRequestDTO);

            InvokeEndpointRequest invokeEndpointRequest = new InvokeEndpointRequest()
                    .withEndpointName(endpointName) // endpointName 직접 주입
                    .withContentType("application/json")
                    .withBody(ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8)));

            InvokeEndpointResult invokeEndpointResult = sagemakerRuntimeClient.invokeEndpoint(invokeEndpointRequest);


            String responseBody = StandardCharsets.UTF_8.decode(invokeEndpointResult.getBody()).toString();
            log.info("SageMaker 응답: {}", responseBody);
            ModelResponseDTO responseDTO = objectMapper.readValue(responseBody, ModelResponseDTO.class);

            processModelResponse(responseDTO, meetingId);

        } catch (Exception e) {
            log.error("모델 서버 호출 중 오류 발생: {}", e.getMessage(), e);
        } finally {
            closeMp3File(mp3File);
        }
    }

    private void processModelResponse(ModelResponseDTO response, Long meetingId) {
        log.info("processModelResponse 실행");

        meetingService.endMeeting(Status.COMPLETE, meetingId);
        try {
            Map<String, String> imageUrlMap = s3FileService.transferZipContentFromOtherS3(
                    modelServerBucketName, response.diagram_image(), "images", meetingId);
            for (Map.Entry<String, String> entry : imageUrlMap.entrySet()) {
                String fileName = entry.getKey();
                String imageUrl = entry.getValue();
                meetingFileService.create(meetingId, FileType.IMAGE, imageUrl, fileName);
            }

            String reportUrl = s3FileService.processAndUploadMarkdownFile(
                    modelServerBucketName, response.report(), "report", meetingId, imageUrlMap);
            String reportName = meetingId + "_" + response.report().substring(response.report().lastIndexOf("/") + 1);
            meetingFileService.create(meetingId, FileType.REPORT, reportUrl, reportName);

            String sttUrl = s3FileService.transferFileFromOtherS3(
                    modelServerBucketName, response.stt(), "stt", meetingId);
            String sttFileName = meetingId + "_" + response.stt().substring(response.stt().lastIndexOf("/") + 1);
            meetingFileService.create(meetingId, FileType.STT_RAW, sttUrl, sttFileName);

        } catch (Exception e) {
            log.error("파일 다운 중 에러 발생: {}", e.getMessage());
        }
    }

    private void closeMp3File(MultipartFile mp3File) {
        try {
            if (mp3File != null) {
                mp3File.getInputStream().close();
            }
        } catch (IOException e) {
            log.error("MP3 파일 스트림 닫기 실패: {}", e.getMessage());
        }
    }
}