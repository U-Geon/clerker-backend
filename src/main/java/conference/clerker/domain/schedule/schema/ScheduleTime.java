package conference.clerker.domain.schedule.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ScheduleTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, name = "schedule_time_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    @JsonIgnore
    private Schedule schedule;

    @OneToMany(mappedBy = "scheduleTime", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TimeTable> timeTables;

    // FK처럼 쓰지말고 member id만 담아두면 될듯
    private Long memberId;

    public static ScheduleTime create(Schedule schedule, Long memberId) {
        return ScheduleTime.builder()
                .timeTables(new ArrayList<>())
                .schedule(schedule)
                .memberId(memberId)
                .build();
    }
}