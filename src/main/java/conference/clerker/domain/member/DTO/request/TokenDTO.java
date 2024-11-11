package conference.clerker.domain.member.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record TokenDTO (
        @NotBlank String accessToken
) {}
