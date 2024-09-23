package conference.clerker.domain.meeting.repository;

import conference.clerker.domain.meeting.schema.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByProjectId(Long projectId);
}
