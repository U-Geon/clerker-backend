package conference.clerker.domain.schedule.schema;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.project.schema.Project;
import conference.clerker.domain.schedule.dto.request.CreateScheduleRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, name = "schedule_id")
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // 년 월 일만 저장

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // 시 분 초만 저장

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_ended")
    private Boolean isEnded;

    @Column(name = "name")
    private String name;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "schedule")
    private List<ScheduleTime> scheduleTimes = new ArrayList<>();

    public static Schedule create(Project project, Member member, CreateScheduleRequestDTO requestDTO) {
        return Schedule.builder()
                .project(project)
                .member(member)
                .startDate(requestDTO.startDate())
                .endDate(requestDTO.endDate())
                .startTime(requestDTO.startTime())
                .endTime(requestDTO.endTime())
                .name(requestDTO.name())
                .build();
    }

}
