package conference.clerker.domain.meeting.dto.response;

import conference.clerker.domain.meeting.schema.Meeting;
import java.time.LocalDateTime;

public record FindMeetingsDTO(
        String name,
        LocalDateTime startDate,
        Boolean isEnded,
        LocalDateTime createdAt
) {
    public FindMeetingsDTO(Meeting meeting) {
        this(
                meeting.getName(),
                meeting.getStartDate(),
                meeting.getIsEnded(),
                meeting.getCreatedAt()
        );
    }
}
