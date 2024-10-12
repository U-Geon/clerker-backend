package conference.clerker.domain.meeting.schema;

import conference.clerker.domain.meeting.dto.request.CreateMeetingRequestDTO;
import conference.clerker.domain.project.schema.Project;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Meeting {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, name = "meeting_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startDate;

    private Boolean isEnded;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public static Meeting create(Project project, String name, String url, CreateMeetingRequestDTO requestDTO) {
        return Meeting.builder()
                .name(name)
                .project(project)
                .url(url)
                .startDate(requestDTO.startDate())
                .isEnded(false)
                .build();
    }
}
