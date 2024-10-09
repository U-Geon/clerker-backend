package conference.clerker.domain.schedule.repository;

import conference.clerker.domain.schedule.schema.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
}
