package conference.clerker.domain.schedule.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateScheduleRequestDTO(
        @NotBlank
        String name,
        @NotBlank
        LocalDate startDate,
        @NotBlank
        LocalDate endDate,
        @NotBlank
        Boolean isNotify,
        @NotBlank
        LocalTime startTime,
        @NotBlank
        LocalTime endTime
) {}
