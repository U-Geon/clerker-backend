package conference.clerker.domain.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
        String startTime,
        @NotBlank
        String endTime
) {
        @JsonIgnore
        public LocalTime getStartTimeAsLocalTime() {
                return LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        }

        @JsonIgnore
        public LocalTime getEndTimeAsLocalTime() {
                return LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
}
