package conference.clerker.domain.meeting.controller;

import conference.clerker.domain.meeting.dto.request.CreateMeetingRequestDTO;
import conference.clerker.domain.meeting.schema.Meeting;
import conference.clerker.domain.meeting.service.MeetingService;
import conference.clerker.domain.notification.service.NotificationService;
import conference.clerker.domain.organization.service.OrganizationService;
import conference.clerker.global.oauth2.service.OAuth2UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class MeetingController {

    private final MeetingService meetingService;
    private final OrganizationService organizationService;
    private final NotificationService notificationService;


    @PostMapping("/create/{projectID}")
    @Operation(summary = "미팅 생성 API", description = "1. 알림 생성 체크 시 프로젝트 내 멤버들에게 알림 생성\n2. startDateTime은 YYYY-MM-DDTHH:mm ex)2024-10-17T09:00")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "PROJECT-001", description = "프로젝트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "AUTH-001", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> createMeeting(
            @AuthenticationPrincipal OAuth2UserPrincipal principal,
            @Parameter(required = true, description = "프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @RequestBody CreateMeetingRequestDTO requestDTO) {
        meetingService.create(projectId, requestDTO);
        if (requestDTO.isNotify()) {
            organizationService.findMembersByProjectId(projectId).forEach(
                    target -> notificationService.notify(target.getId(),
                            projectId,
                            requestDTO.name(),
                            requestDTO.startDateTime(),
                            "회의"
                    )
            );
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/detail/{meetingID}")
    @Operation(summary = "회의 상세 조회 API", description = "회의가 종료되기 전 회의 링크와 회의 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "MEETING-001", description = "회의를 찾을 수 없습니다", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Meeting> detailMeeting(
            @Parameter(required = true, description = "회의 ID", in = ParameterIn.PATH)
            @PathVariable("meetingID") Long meetingId) {

       return ResponseEntity.ok().body(meetingService.findById(meetingId));
    }

}
