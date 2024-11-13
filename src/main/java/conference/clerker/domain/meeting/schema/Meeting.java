package conference.clerker.domain.meeting.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "domain")
    private String domain;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "start_date", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "is_ended", nullable = false)
    private Boolean isEnded;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    public static Meeting create(Project project, String name, String url, CreateMeetingRequestDTO requestDTO) {
        return Meeting.builder()
                .name(name)
                .domain(requestDTO.domain())
                .project(project)
                .url(url)
                .startDate(requestDTO.startDateTime())
                .isEnded(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
