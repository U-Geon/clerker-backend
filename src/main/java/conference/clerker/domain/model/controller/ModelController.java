package conference.clerker.domain.model.controller;

import conference.clerker.domain.model.dto.response.ModelResponseDTO;
import conference.clerker.domain.model.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/model")
public class ModelController {

    private final ModelService modelService;

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "영상 녹화 종료 시 API 요청",
            description = ".webm 파일을 mp3로 변환 후 S3에 저장. 모델에 S3 url과 주제 도메인 전송"
    )
    public Mono<ResponseEntity<ModelResponseDTO>> endRecording(
            @Parameter(description = "주제 도메인", required = true, example = "\"Front-end\"")
            @Valid @RequestPart("domain") String domain,

            @Parameter(description = ".webm 형식의 녹화 파일", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @Valid @RequestPart("webmFile") MultipartFile webmFile,

            @Parameter(description = "meeting ID", required = true)
            @Valid @RequestParam(value = "meetingId") Long meetingId
    ) {
        return modelService.sendToModelServer(domain, webmFile, meetingId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PostMapping("/testProcessResponse")
    @Operation(
            summary = "processModelResponse 메서드 테스트",
            description = "테스트 데이터를 사용하여 processModelResponse 메서드를 실행합니다."
    )
    public ResponseEntity<String> testProcessResponse(
            @RequestParam(name = "meetingId") Long meetingId, // 파라미터 이름 명시
            @RequestBody ModelResponseDTO response
    ) {
        try {
            modelService.testProcessModelResponse(response, meetingId);
            return ResponseEntity.ok("processModelResponse 메서드가 성공적으로 실행되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("processModelResponse 실행 중 오류 발생: " + e.getMessage());
        }
    }


//    @PostMapping("/test1")
//    @Operation(summary = "webm to mp3 이후 s3 url 발급 테스트")
//    public String test1(
//            @RequestPart("webmFile") @Valid MultipartFile webmFile
//    ) {
//        return modelService.test1(webmFile);
//    }
}
