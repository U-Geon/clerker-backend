package conference.clerker.domain.organization.dto;

import conference.clerker.domain.organization.schema.Role;

public record MemberInfoDTO(
        Long organizationId,
        String username,
        String email,
        Role role,
        String type
) {}
