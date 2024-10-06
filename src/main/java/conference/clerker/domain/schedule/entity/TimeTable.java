package conference.clerker.domain.schedule.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeTable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, name = "time_table_id")
    private Long id;

    @Column(nullable = false)
    private String time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_time_id")
    @JsonIgnore
    private ScheduleTime scheduleTime;

    private void setScheduleTime(ScheduleTime scheduleTime) {
        this.scheduleTime = scheduleTime;
        if(scheduleTime.getTimeTables() == null) {
            scheduleTime.setTimeTables(new ArrayList<>());
        }
        scheduleTime.getTimeTables().add(this);
    }

    public static TimeTable create(ScheduleTime scheduleTime, String time) {
        TimeTable timeTable = new TimeTable();
        timeTable.setScheduleTime(scheduleTime);
        timeTable.setTime(time);
        return timeTable;
    }
}