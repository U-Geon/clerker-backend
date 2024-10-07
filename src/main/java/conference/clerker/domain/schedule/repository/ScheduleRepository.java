package conference.clerker.domain.schedule.repository;

import conference.clerker.domain.schedule.schema.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByProjectId(Long projectId);

    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") Long projectId);
}
