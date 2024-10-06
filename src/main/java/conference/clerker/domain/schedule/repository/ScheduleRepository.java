package conference.clerker.domain.schedule.repository;

import conference.clerker.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByProjectId(Long projectId);
}
