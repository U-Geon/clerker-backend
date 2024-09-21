package conference.clerker.domain.schedule.dto.response;

import java.util.List;

public record ScheduleTimeWithMemberInfoDTO(
        List<String> timeTable,
        String username,
        String email,
        String phoneNumber,
        String type
) {}
