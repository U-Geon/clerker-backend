package conference.clerker.domain.meeting.dtos.response;

import conference.clerker.domain.meeting.entity.Meeting;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class FindMeetingsDTO {
    private String name;
    private LocalDateTime startDate;
    private Boolean isEnded;
    private LocalDateTime createdAt;

    public FindMeetingsDTO(Meeting meeting) {
        this.name = meeting.getName();
        this.startDate = meeting.getStartDate();
        this.isEnded = meeting.getIsEnded();
        this.createdAt = meeting.getCreatedAt();
    }
}
