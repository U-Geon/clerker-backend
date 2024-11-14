package conference.clerker.domain.meeting.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record CreateMeetingRequestDTO (
        @NotBlank
        String name,

        @NotBlank
        String startDateTime,

        @NotBlank
        String domain,

        @NotNull
        Boolean isNotify
) {
        @JsonIgnore
        public LocalDateTime getStartTimeAsLocalTime() {
                return LocalDateTime.parse(startDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        }
}
