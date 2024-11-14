package conference.clerker.domain.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ModelRequestDTO(
        @NotEmpty
        String domain,
        @NotBlank
        String mp3FileUrl,
        @NotBlank
        Long meetingId
) {}
