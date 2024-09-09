package conference.clerker.domain.schedule.controller;


import conference.clerker.domain.member.entity.Member;
import conference.clerker.domain.schedule.dtos.request.CreateScheduleRequestDTO;
import conference.clerker.domain.schedule.entity.Schedule;
import conference.clerker.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/create/{projectID}")
    @Operation(summary = "스케쥴 생성하기", description = "스케쥴 생성 API")
    public void createSchedule(
            @AuthenticationPrincipal Member member,
            @Parameter(required = true, description = "부모 프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @RequestBody CreateScheduleRequestDTO requestDTO) {
        scheduleService.create(projectId, member.getId(), requestDTO);
    }
}
