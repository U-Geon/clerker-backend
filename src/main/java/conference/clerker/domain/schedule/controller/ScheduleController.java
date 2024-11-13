package conference.clerker.domain.schedule.controller;


import conference.clerker.domain.meeting.dto.response.FindMeetingsDTO;
import conference.clerker.domain.meeting.service.MeetingService;
import conference.clerker.domain.notification.service.NotificationService;
import conference.clerker.domain.organization.service.OrganizationService;
import conference.clerker.domain.schedule.dto.request.CreateScheduleRequestDTO;
import conference.clerker.domain.schedule.dto.response.FindSchedulesDTO;
import conference.clerker.domain.schedule.dto.request.JoinScheduleRequestDTO;
import conference.clerker.domain.schedule.dto.response.ScheduleTimeWithMemberInfoDTO;
import conference.clerker.domain.schedule.dto.response.SchedulesAndMeetingsListResponseDTO;
import conference.clerker.domain.schedule.service.ScheduleService;
import conference.clerker.domain.schedule.service.ScheduleTimeService;
import conference.clerker.global.oauth2.service.OAuth2UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final MeetingService meetingService;
    private final ScheduleTimeService scheduleTimeService;
    private final NotificationService notificationService;
    private final OrganizationService organizationService;

    @PostMapping("/create/{projectID}")
    @Operation(summary = "스케쥴 생성 API", description = "1. 알림 생성 체크 시 프로젝트 내 멤버들에게 알림 생성\n2. time은 HH:MM:SS 식으로 보내주세요!")
    public ResponseEntity<Void> createSchedule(
            @AuthenticationPrincipal OAuth2UserPrincipal principal,
            @Parameter(required = true, description = "프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @RequestBody CreateScheduleRequestDTO requestDTO) {
        scheduleService.create(projectId, principal.getMember().getId(), requestDTO);
        if (requestDTO.isNotify()) {
            organizationService.findMembersByProjectId(projectId).forEach(
                    target -> notificationService.notify(target.getId(),
                            projectId,
                            requestDTO.name(),
                            requestDTO.startDate(),
                            requestDTO.endDate(),
                            "회의 스케쥴")
            );
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectID}")
    @Operation(summary = "프로젝트 캘린더 목록 API", description = "특정 프로젝트의 Meeting 및 Schedule 목록")
    public ResponseEntity<SchedulesAndMeetingsListResponseDTO> findAllSchedules(
            @Parameter(required = true, description = "프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        List<FindSchedulesDTO> scheduleList = scheduleService.findByProjectId(projectId);
        List<FindMeetingsDTO> meetingList = meetingService.findByProjectId(projectId);
        return ResponseEntity.ok().body(new SchedulesAndMeetingsListResponseDTO(scheduleList, meetingList));
    }

    @PostMapping("/{scheduleID}")
    @Operation(summary = "개인별 스케쥴 참여 API", description = "시간표 드래그 후 입력 시 요청, 30분 단위로 보내주세용")
    public ResponseEntity<Void> joinSchedule(
            @Parameter(required = true, description = "스케쥴 ID", in = ParameterIn.PATH)
            @PathVariable("scheduleID") Long scheduleId,
            @AuthenticationPrincipal OAuth2UserPrincipal principal,
            @RequestBody JoinScheduleRequestDTO requestDTO) {

        scheduleTimeService.create(scheduleId, requestDTO.timeTable(), principal.getMember().getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectID}/detail/{scheduleID}")
    @Operation(summary = "스케쥴 상세 조회 API", description = "개인별 입력한 스케쥴 시간 List + 참여한 인원 List 조회")
    public ResponseEntity<List<ScheduleTimeWithMemberInfoDTO>> detailSchedule(
            @Parameter(required = true, description = "프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @Parameter(required = true, description = "스케쥴 ID", in = ParameterIn.PATH)
            @PathVariable("scheduleID") Long scheduleId) {
        return ResponseEntity.ok().body(scheduleTimeService.findScheduleTimeAndMemberInfo(scheduleId, projectId));
    }
}
