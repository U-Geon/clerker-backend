package conference.clerker.domain.meeting.controller;

import conference.clerker.domain.meeting.dto.request.CreateMeetingRequestDTO;
import conference.clerker.domain.meeting.dto.response.MeetingResultDTO;
import conference.clerker.domain.meeting.schema.Meeting;
import conference.clerker.domain.meeting.service.MeetingService;
import conference.clerker.domain.notification.service.NotificationService;
import conference.clerker.domain.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> createMeeting(
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
    @Operation(summary = "회의 상세 조회 API", description = "회의 종료 전 : 회의 링크와 회의 정보 조회\n\n회의 종료 후 : 상세 페이지로 리디렉션")
    public ResponseEntity<?> detailMeeting(
            @Parameter(required = true, description = "회의 ID", in = ParameterIn.PATH)
            @PathVariable("meetingID") Long meetingId) {

        Meeting meeting = meetingService.findById(meetingId);

        if (meeting.getIsEnded()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(meetingService.redirectToMeetingDetailPage(meetingId));
        }
        return ResponseEntity.ok(meeting);
    }

    @GetMapping("/result/{meetingID}")
    @Operation(summary = "회의 결과 조회 API", description = "회의가 종료된 후 생성된 결과물 조회")
    public ResponseEntity<MeetingResultDTO> meetingReport(
            @Parameter(required = true, description = "회의 ID", in = ParameterIn.PATH)
            @PathVariable("meetingID") Long meetingId) {

        return ResponseEntity.ok().body(meetingService.findMeetingFiles(meetingId));
    }

}
