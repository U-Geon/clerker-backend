package conference.clerker.domain.organization.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MemberInfoDTO {
    private Long organizationId;
    private String username;
    private String email;
    private String phoneNumber;
    private String role;
    private String type;
}