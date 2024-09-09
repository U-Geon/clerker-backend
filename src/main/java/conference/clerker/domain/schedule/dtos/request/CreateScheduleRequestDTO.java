package conference.clerker.domain.schedule.dtos.request;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
public class CreateScheduleRequestDTO {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isNotify;
    private LocalTime startTime;
    private LocalTime endTime;
}
