package conference.clerker.domain.schedule.repository;

import conference.clerker.domain.schedule.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
}
