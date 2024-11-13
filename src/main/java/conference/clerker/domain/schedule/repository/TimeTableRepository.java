package conference.clerker.domain.schedule.repository;

import conference.clerker.domain.schedule.schema.ScheduleTime;
import conference.clerker.domain.schedule.schema.TimeTable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    void deleteAllByScheduleTime(ScheduleTime scheduleTime);
}
