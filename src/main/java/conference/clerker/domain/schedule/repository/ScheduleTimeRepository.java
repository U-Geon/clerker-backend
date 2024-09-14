package conference.clerker.domain.schedule.repository;

import conference.clerker.domain.schedule.entity.ScheduleTime;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleTimeRepository extends JpaRepository<ScheduleTime, Long> {

}
