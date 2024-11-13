package conference.clerker.domain.meeting.dto.response;

import conference.clerker.domain.meeting.schema.FileType;


import java.util.List;
import java.util.Map;

public record MeetingResultDTO(
        Long meetingId,
        String name,
        String domain,
        Map<FileType, List<MeetingFIleDTO>> files
) {}
