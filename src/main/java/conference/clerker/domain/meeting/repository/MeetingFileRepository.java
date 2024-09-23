package conference.clerker.domain.meeting.repository;

import conference.clerker.domain.meeting.schema.MeetingFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingFileRepository extends JpaRepository<MeetingFile, Long> {
}
