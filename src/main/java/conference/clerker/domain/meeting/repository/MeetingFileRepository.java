package conference.clerker.domain.meeting.repository;

import conference.clerker.domain.meeting.entity.MeetingFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingFileRepository extends JpaRepository<MeetingFile, Long> {
}
