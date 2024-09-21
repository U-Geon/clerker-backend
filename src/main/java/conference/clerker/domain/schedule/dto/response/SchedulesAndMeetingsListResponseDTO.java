package conference.clerker.domain.schedule.dto.response;

import conference.clerker.domain.meeting.dto.response.FindMeetingsDTO;
import java.util.List;

public record SchedulesAndMeetingsListResponseDTO(
        List<FindSchedulesDTO> schedules,
        List<FindMeetingsDTO> meetings
) {}
