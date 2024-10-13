package conference.clerker.domain.meeting.schema;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MeetingFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, name = "meeting_file_id")
    private Long id;

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;

    @Column(name = "filename")
    private String filename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    public static MeetingFile create(Meeting meeting) {
        return MeetingFile.builder()
                .meeting(meeting)
                .build();
    }
}
