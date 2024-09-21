package conference.clerker.domain.organization.dto;

public record MemberInfoDTO(
        Long organizationId,
        String username,
        String email,
        String phoneNumber,
        String role,
        String type
) {}
