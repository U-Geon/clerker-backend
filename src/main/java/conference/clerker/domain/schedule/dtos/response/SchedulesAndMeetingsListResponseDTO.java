package conference.clerker.domain.schedule.dtos.response;

import conference.clerker.domain.meeting.dtos.response.FindMeetingsDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class SchedulesAndMeetingsListResponseDTO {
    private List<FindSchedulesDTO> schedules;
    private List<FindMeetingsDTO> meetings;
}
