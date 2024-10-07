package conference.clerker.domain.schedule.schema;

import jakarta.persistence.*;
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

    @Column(name = "time_table", nullable = false)
    @ElementCollection
    private List<String> timeTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    // FK처럼 쓰지말고 member id만 담아두면 될듯
    private Long memberId;

    public static ScheduleTime create(List<String> timeTable, Schedule schedule, Long memberId) {
        ScheduleTime scheduleTime = new ScheduleTime();
        scheduleTime.setTimeTable(timeTable);
        scheduleTime.setScheduleTime(schedule);
        scheduleTime.setMemberId(memberId);
        return scheduleTime;
    }

    public void setScheduleTime(Schedule schedule) {
        schedule.getScheduleTimes().add(this);
        this.schedule = schedule;
    }

    public void removeScheduleTime(Schedule schedule) {
        schedule.getScheduleTimes().remove(this);
    }
}
