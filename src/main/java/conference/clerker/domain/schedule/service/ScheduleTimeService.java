package conference.clerker.domain.schedule.service;


import conference.clerker.domain.schedule.dto.response.ScheduleTimeWithMemberInfoDTO;
import conference.clerker.domain.schedule.schema.Schedule;
import conference.clerker.domain.schedule.schema.ScheduleTime;
import conference.clerker.domain.schedule.repository.ScheduleRepository;
import conference.clerker.domain.schedule.repository.ScheduleTimeRepository;
import conference.clerker.domain.schedule.repository.TimeTableRepository;
import conference.clerker.domain.schedule.schema.TimeTable;
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
    private final TimeTableRepository timeTableRepository;

    // 개인별 스케쥴 기록 생성.
    @Transactional
    public void create(Long scheduleId, List<String> timeTable, Long memberId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 해당 사용자가 등록한 타임 테이블에서 중복된 시간을 등록했는지 체크
        schedule.getScheduleTimes().stream()
                .filter(scheduleTime -> scheduleTime.getMemberId().equals(memberId))
                .findFirst()
                .ifPresent(scheduleTime -> {
                    List<String> existingTimes = scheduleTime.getTimeTables().stream()
                            .map(TimeTable::getTime).toList();

                    for (String time : timeTable) {
                        if (existingTimes.contains(time)) {
                            throw new ScheduleException(ErrorCode.DUPLICATE_TIME);
                        }
                    }
                });

        ScheduleTime scheduleTime = ScheduleTime.create(schedule, memberId);
        for (String time : timeTable) {
            TimeTable entity = TimeTable.create(scheduleTime, time);
            timeTableRepository.save(entity);
        }
        scheduleTimeRepository.save(scheduleTime);
    }

    // 해당 스케쥴 상세 조회
    public List<ScheduleTimeWithMemberInfoDTO> findScheduleTimeAndMemberInfo(Long scheduleId, Long projectId) {
        return scheduleTimeRepository.findScheduleTimeWithMemberInfoByScheduleIdAndProjectId(scheduleId, projectId);
    }

}
