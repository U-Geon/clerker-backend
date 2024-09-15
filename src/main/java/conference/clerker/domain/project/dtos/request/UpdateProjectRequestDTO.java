package conference.clerker.domain.project.dtos.request;


import conference.clerker.domain.organization.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UpdateProjectRequestDTO {
    @NotBlank
    private String projectName;
    private List<OrganizationInfo> members;

    @Getter @Setter
    public static class OrganizationInfo {
        private Long organizationId;
        private String memberName;
        private Role role;
        private String email;
        private String phoneNumber;
        private String type;
    }
}
