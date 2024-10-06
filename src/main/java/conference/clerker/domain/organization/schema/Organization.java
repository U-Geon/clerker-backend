package conference.clerker.domain.organization.schema;


import com.fasterxml.jackson.annotation.JsonIgnore;
import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.project.schema.Project;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Organization {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, name = "organizaion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "type")
    private String type;

    public void setProject(Project project) {
        this.project = project;
        if (project.getOrganizations() == null) {
            project.setOrganizations(new ArrayList<>()); // 초기화
        }
        project.getOrganizations().add(this);
    }

    public void removeProject(Project project) {
        project.getOrganizations().remove(this);
    }

    public static Organization createMember(Member member, Project project) {
        Organization organization = new Organization();
        organization.setMember(member);
        organization.setProject(project);
        organization.setRole(Role.MEMBER);
        return organization;
    }

    public static Organization createOwner(Member member, Project project) {
        Organization organization = new Organization();
        organization.setMember(member);
        organization.setProject(project);
        organization.setRole(Role.OWNER);
        return organization;
    }
}
