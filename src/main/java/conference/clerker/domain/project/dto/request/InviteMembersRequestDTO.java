package conference.clerker.domain.project.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record InviteMembersRequestDTO(
        @NotEmpty
        List<String> emails
) {}
