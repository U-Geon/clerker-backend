package conference.clerker.domain.schedule.service;

import conference.clerker.domain.member.entity.Member;
import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.project.entity.Project;
import conference.clerker.domain.project.repository.ProjectRepository;
import conference.clerker.domain.schedule.dtos.request.CreateScheduleRequestDTO;
import conference.clerker.domain.schedule.dtos.response.FindSchedulesDTO;
import conference.clerker.domain.schedule.dtos.request.JoinScheduleRequestDTO;
import conference.clerker.domain.schedule.entity.Schedule;
import conference.clerker.domain.schedule.repository.ScheduleRepository;
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
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AuthException(ErrorCode.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new AuthException(ErrorCode.PROJECT_NOT_FOUND));
        Schedule schedule = Schedule.create(project, member, requestDTO);
        scheduleRepository.save(schedule);
    }

    // project ID를 통한 스케쥴 조회
    public List<FindSchedulesDTO> findByProjectId(Long projectId) {
        return scheduleRepository.findByProjectId(projectId).stream().map(FindSchedulesDTO::new).toList();
    }

    // 스케쥴 참여
    @Transactional
    public void participate(Long projectId, Long memberId, JoinScheduleRequestDTO requestDTO) {

    }

}
