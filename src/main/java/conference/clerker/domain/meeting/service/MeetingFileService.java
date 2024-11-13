package conference.clerker.domain.meeting.service;

import conference.clerker.domain.meeting.repository.MeetingFileRepository;
import conference.clerker.domain.meeting.repository.MeetingRepository;
import conference.clerker.domain.meeting.schema.FileType;
import conference.clerker.domain.meeting.schema.Meeting;
import conference.clerker.domain.meeting.schema.MeetingFile;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingFileService {

    private final MeetingRepository meetingRepository;
    private final MeetingFileRepository meetingFileRepository;

    @Transactional
    public MeetingFile create(Long meetingId, FileType fileType, String url, String name){
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new AuthException(ErrorCode.MEETING_NOT_FOUND));

        MeetingFile meetingFIle = MeetingFile.create(meeting, fileType, url, name);

        return meetingFileRepository.save(meetingFIle);
    }
}
