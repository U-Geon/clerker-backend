package conference.clerker.domain.schedule.dto.response;

import conference.clerker.domain.schedule.schema.Schedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record FindSchedulesDTO(
        Long scheduleId,
        String scheduleName,
        LocalDate startDate, // 연 월 일
        LocalDate endDate,
        LocalTime startTime, // 시 분 초
        LocalTime endTime,
        LocalDateTime createdAt,
        Boolean isEnded
) {
    public FindSchedulesDTO(Schedule schedule) {
        this(
                schedule.getId(),
                schedule.getName(),
                schedule.getStartDate(),
                schedule.getEndDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getCreatedAt(),
                schedule.getIsEnded()
        );
    }
}
