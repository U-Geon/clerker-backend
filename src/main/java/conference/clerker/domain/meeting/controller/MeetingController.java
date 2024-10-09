package conference.clerker.domain.meeting.controller;

import conference.clerker.domain.meeting.dto.request.CreateMeetingRequestDTO;
import conference.clerker.domain.meeting.service.MeetingService;
import conference.clerker.domain.member.schema.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/create/{projectID}")
    @Operation(summary = "미팅 생성 API")
    public ResponseEntity<void> createMeeting(
            @AuthenticationPrincipal Member member,
            @Parameter(required = true, description = "프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @RequestBody CreateMeetingRequestDTO requestDTO) {
        meetingService.create(projectId, requestDTO);
        if (requestDTO.i)
    }
}
