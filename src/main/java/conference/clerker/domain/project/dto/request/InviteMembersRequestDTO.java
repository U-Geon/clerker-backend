package conference.clerker.domain.project.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record InviteMembersRequestDTO(
        @NotEmpty(message = "이메일이 비어있습니다.")
        List<String> emails
) {}
