package conference.clerker.domain.organization.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ProjectInfoDTO {
    private String projectName;
    private List<MemberInfoDTO> members;
}
