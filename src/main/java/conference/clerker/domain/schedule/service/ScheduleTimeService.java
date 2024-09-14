package conference.clerker.domain.schedule.service;


import conference.clerker.domain.schedule.dtos.response.ScheduleTimeWithMemberInfoDTO;
import conference.clerker.domain.schedule.entity.Schedule;
import conference.clerker.domain.schedule.entity.ScheduleTime;
import conference.clerker.domain.schedule.repository.ScheduleRepository;
import conference.clerker.domain.schedule.repository.ScheduleTimeRepository;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.ScheduleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleTimeService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleTimeRepository scheduleTimeRepository;

    // 개인별 스케쥴 기록 생성.
    @Transactional
    public void create(Long scheduleId, List<String> timeTable, Long memberId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));
        ScheduleTime scheduleTime = ScheduleTime.create(timeTable, schedule, memberId);
        scheduleTimeRepository.save(scheduleTime);
    }

    // 해당 스케쥴 상세 조회
//    public void findTimeTableAndMemberInfoInSchedule(Long scheduleId, Long projectId) {
//        return scheduleTimeRepository.findScheduleTimeWithMemberInfoByScheduleIdAndProjectId(scheduleId, projectId)
//                .stream().map(ScheduleTimeWithMemberInfoDTO::new);
//    }

}
