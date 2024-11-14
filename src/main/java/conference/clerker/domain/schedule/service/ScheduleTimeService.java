package conference.clerker.domain.schedule.service;


import conference.clerker.domain.schedule.dto.response.ScheduleTimeWithMemberInfoDTO;
import conference.clerker.domain.schedule.schema.Schedule;
import conference.clerker.domain.schedule.schema.ScheduleTime;
import conference.clerker.domain.schedule.repository.ScheduleRepository;
import conference.clerker.domain.schedule.repository.ScheduleTimeRepository;
import conference.clerker.domain.schedule.repository.TimeTableRepository;
import conference.clerker.domain.schedule.schema.TimeTable;
import conference.clerker.global.exception.CustomException;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.ScheduleException;
import jakarta.validation.ConstraintViolationException;
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
    private final TimeTableRepository timeTableRepository;

    // 개인별 스케쥴 기록 생성.
    @Transactional
    public void create(Long scheduleId, List<String> timeTable, Long memberId) {
        try {
            Schedule schedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

            ScheduleTime scheduleTime = schedule.getScheduleTimes().stream()
                    .filter(st -> st.getMemberId().equals(memberId))
                    .findFirst()
                    .orElse(null);

            if (scheduleTime == null) {
                scheduleTime = ScheduleTime.create(schedule, memberId);
                scheduleTimeRepository.save(scheduleTime);  // 먼저 영속화
                schedule.getScheduleTimes().add(scheduleTime);  // schedule에 추가
            } else {
                scheduleTime.getTimeTables().clear();
                timeTableRepository.deleteAllByScheduleTime(scheduleTime);
            }

            for (String time : timeTable) {
                TimeTable entity = TimeTable.create(scheduleTime, time);
                timeTableRepository.save(entity);
                scheduleTime.getTimeTables().add(entity);
            }

            scheduleRepository.save(schedule);
        } catch (ConstraintViolationException e) {
            throw new CustomException(ErrorCode.BODY_NOT_EMPTY);
        }
    }

    // 해당 스케쥴 상세 조회
    public List<ScheduleTimeWithMemberInfoDTO> findScheduleTimeAndMemberInfo(Long scheduleId, Long projectId) {
        return scheduleTimeRepository.findScheduleTimeWithMemberInfoByScheduleIdAndProjectId(scheduleId, projectId);
    }

}
