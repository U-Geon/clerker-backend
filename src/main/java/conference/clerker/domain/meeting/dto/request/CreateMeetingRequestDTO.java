package conference.clerker.domain.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateMeetingRequestDTO (
        @NotBlank
        String name,
        @NotBlank
        LocalDateTime startDate
) {

}