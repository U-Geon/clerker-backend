package conference.clerker.domain.schedule.dtos.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
public class CreateScheduleRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private LocalDate startDate;
    @NotBlank
    private LocalDate endDate;
    @NotBlank
    private Boolean isNotify;
    @NotBlank
    private LocalTime startTime;
    @NotBlank
    private LocalTime endTime;
}
