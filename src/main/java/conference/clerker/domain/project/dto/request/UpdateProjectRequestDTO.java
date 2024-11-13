package conference.clerker.domain.project.dto.request;

import conference.clerker.domain.organization.schema.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateProjectRequestDTO(
        @NotBlank
        String projectName,
        @NotNull
        List<OrganizationInfo> members
) {
    public record OrganizationInfo(
            @NotBlank
            Long organizationId,
            @NotBlank
            Role role,
            @NotBlank
            String type
    ) {}
}
