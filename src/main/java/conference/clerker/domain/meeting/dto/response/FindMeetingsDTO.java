package conference.clerker.domain.meeting.dto.response;

import conference.clerker.domain.meeting.schema.Meeting;
import conference.clerker.domain.meeting.schema.Status;
import java.time.LocalDateTime;

public record FindMeetingsDTO(
        Long meetingId,
        String name,
        String domain,
        String url,
        LocalDateTime startDate,
        Status status,
        LocalDateTime createdAt
) {
    public FindMeetingsDTO(Meeting meeting) {
        this(
                meeting.getId(),
                meeting.getName(),
                meeting.getDomain(),
                meeting.getUrl(),
                meeting.getStartDate(),
                meeting.getStatus(),
                meeting.getCreatedAt()
        );
    }
}
