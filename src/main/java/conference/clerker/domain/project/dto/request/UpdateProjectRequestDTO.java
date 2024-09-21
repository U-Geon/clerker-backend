package conference.clerker.domain.project.dto.request;

import conference.clerker.domain.organization.entity.Role;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record UpdateProjectRequestDTO(
        @NotBlank
        String projectName,
        List<OrganizationInfo> members
) {
    public record OrganizationInfo(
            Long organizationId,
            String memberName,
            Role role,
            String email,
            String phoneNumber,
            String type
    ) {}
}
