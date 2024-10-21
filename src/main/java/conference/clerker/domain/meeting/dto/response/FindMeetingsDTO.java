package conference.clerker.domain.meeting.dto.response;

import conference.clerker.domain.meeting.schema.Meeting;
import java.time.LocalDateTime;

public record FindMeetingsDTO(
        Long meetingId,
        String name,
        LocalDateTime startDate,
        Boolean isEnded,
        LocalDateTime createdAt
) {
    public FindMeetingsDTO(Meeting meeting) {
        this(
                meeting.getId(),
                meeting.getName(),
                meeting.getStartDate(),
                meeting.getIsEnded(),
                meeting.getCreatedAt()
        );
    }
}
