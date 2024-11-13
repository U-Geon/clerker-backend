package conference.clerker.domain.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record CreateMeetingRequestDTO (
        @NotBlank
        String name,

        @NotBlank
        LocalDateTime startDateTime,

        String domain,

        @NotBlank
        Boolean isNotify
) {

}