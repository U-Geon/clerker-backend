package conference.clerker.domain.meeting.service;

import conference.clerker.domain.meeting.repository.MeetingFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingFileService {

    private final MeetingFileRepository meetingFileRepository;
}
