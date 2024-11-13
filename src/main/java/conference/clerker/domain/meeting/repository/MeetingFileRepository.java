package conference.clerker.domain.meeting.repository;

import conference.clerker.domain.meeting.schema.MeetingFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingFileRepository extends JpaRepository<MeetingFile, Long> {
    List<MeetingFile> findByMeetingId(Long meetingId);
}
