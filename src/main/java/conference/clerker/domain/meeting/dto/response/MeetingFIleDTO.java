package conference.clerker.domain.meeting.dto.response;

import conference.clerker.domain.meeting.schema.MeetingFile;

public record MeetingFIleDTO(
        Long fileId,
        String url
) {
    public MeetingFIleDTO(MeetingFile meetingFile) {
        this(
                meetingFile.getId(),
                meetingFile.getUrl()
        );
    }
}
