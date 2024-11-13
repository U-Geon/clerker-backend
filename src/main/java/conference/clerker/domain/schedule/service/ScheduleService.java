package conference.clerker.domain.schedule.service;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.project.schema.Project;
import conference.clerker.domain.project.repository.ProjectRepository;
import conference.clerker.domain.schedule.dto.request.CreateScheduleRequestDTO;
import conference.clerker.domain.schedule.dto.response.FindSchedulesDTO;
import conference.clerker.domain.schedule.schema.Schedule;
import conference.clerker.domain.schedule.repository.ScheduleRepository;
import conference.clerker.global.exception.CustomException;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    // 스케쥴 생성
    @Transactional
    public void create(Long projectId, Long memberId, CreateScheduleRequestDTO requestDTO) {
        try {
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new AuthException(ErrorCode.MEMBER_NOT_FOUND));
            Project project = projectRepository.findById(projectId).orElseThrow(() -> new AuthException(ErrorCode.PROJECT_NOT_FOUND));
            Schedule schedule = Schedule.create(project, member, requestDTO);
            scheduleRepository.save(schedule);
        } catch (NullPointerException e) {
            throw new CustomException(ErrorCode.BODY_VALUE_NOT_FOUND);
        }
    }

    // project ID를 통한 스케쥴 조회
    public List<FindSchedulesDTO> findByProjectId(Long projectId) {
        return scheduleRepository.findAllByProjectId(projectId).stream().map(FindSchedulesDTO::new).toList();
    }

    @Transactional
    public void deleteAllScheduleByProjectId(Long projectId) {
        scheduleRepository.deleteAllByProjectId(projectId);
    }
}
