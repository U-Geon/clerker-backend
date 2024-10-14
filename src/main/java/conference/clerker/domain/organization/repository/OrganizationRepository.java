package conference.clerker.domain.organization.repository;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.organization.dto.MemberInfoDTO;
import conference.clerker.domain.organization.schema.Organization;
import conference.clerker.domain.project.schema.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    @Query("SELECT DISTINCT o.project FROM Organization o " +
            "LEFT JOIN FETCH o.project.meetings m " +
            "WHERE o.member.id = :memberId AND o.project.parentProject IS NULL")
    List<Project> findProjectsWithEndedMeetingsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT new conference.clerker.domain.organization.dto.MemberInfoDTO(o.id, o.member.username, o.member.email, o.role, o.type) " +
            "FROM Organization o WHERE o.project.id = :projectId")
    List<MemberInfoDTO> findMemberInfosByProjectId(@Param("projectId") Long projectId);


    @Query("SELECT o.member FROM Organization o WHERE o.project.id = :projectId")
    List<Member> findMembersByProjectId(@Param("projectId") Long projectId);

    Optional<Organization> findByMemberIdAndProjectId(Long memberId, Long projectId);

    @Modifying
    @Query("DELETE FROM Organization o WHERE o.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") Long projectId);
}
