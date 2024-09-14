package conference.clerker.domain.schedule.dtos.response;

import conference.clerker.domain.schedule.entity.Schedule;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter @Setter
public class FindSchedulesDTO {
    private Long id;
    private LocalDate startDate; // 년 월 일만 저장
    private LocalDate endDate;
    private LocalTime startTime; // 시 분 초만 저장
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private Boolean isEnded;

    public FindSchedulesDTO(Schedule schedule) {
        this.id = schedule.getId();
        this.startDate = schedule.getStartDate();
        this.endDate = schedule.getEndDate();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.isEnded = schedule.getIsEnded();
        this.createdAt = schedule.getCreatedAt();
    }
}
