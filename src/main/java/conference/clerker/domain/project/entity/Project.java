package conference.clerker.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "parent_project")
    @JoinColumn(name = "project_id")
    private Project parentProject;

    public static Project create() {
        return Project.builder()
                .name("새로운 프로젝트").build();
    }
    public static Project create(Project parentProject) {
        return Project.builder()
                .name("새로운 프로젝트")
                .parentProject(parentProject)
                .build();
    }
}
