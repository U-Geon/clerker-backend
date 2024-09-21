package conference.clerker.domain.organization.repository;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.organization.dto.MemberInfoDTO;
import conference.clerker.domain.organization.entity.Organization;
import conference.clerker.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    @Query("SELECT o.project FROM Organization o WHERE o.member.id = :memberId")
    List<Project> findProjectsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT o.id, o.member.username, o.member.email, o.phoneNumber, o.role, o.type FROM Organization o WHERE o.project.id = :projectId")
    List<MemberInfoDTO> findMemberInfosByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT o.member FROM Organization o WHERE o.project.id = :projectId")
    List<Member> findMembersByProjectId(@Param("projectId") Long projectId);

    Optional<Organization> findByMemberIdAndProjectId(Long memberId, Long projectId);

    void deleteAllByProjectId(Long projectId);
}
