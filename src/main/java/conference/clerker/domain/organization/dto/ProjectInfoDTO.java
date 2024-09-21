package conference.clerker.domain.organization.dto;

import java.util.List;

public record ProjectInfoDTO(
        String projectName,
        List<MemberInfoDTO> members
) {}
