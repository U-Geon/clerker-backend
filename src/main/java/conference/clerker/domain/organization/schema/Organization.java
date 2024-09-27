package conference.clerker.domain.organization.schema;


import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
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

    public static Organization createMember(Member member, Project project) {
        return conference.clerker.domain.organization.schema.Organization.builder()
                .role(Role.MEMBER)
                .member(member)
                .project(project)
                .build();
    }

    public static Organization createOwner(Member member, Project project) {
        return Organization.builder()
                .role(Role.OWNER)
                .member(member)
                .project(project)
                .build();
    }
}
