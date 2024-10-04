package conference.clerker.domain.project.dto.request;

import conference.clerker.domain.organization.schema.Role;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record UpdateProjectRequestDTO(
        @NotBlank
        String projectName,
        List<OrganizationInfo> members
) {
    public record OrganizationInfo(
            Long organizationId,
            Role role,
            String type
    ) {}
}
