package conference.clerker.domain.schedule.dto.response;

import conference.clerker.domain.schedule.entity.ScheduleTime;
import conference.clerker.domain.schedule.entity.TimeTable;

import java.util.List;

public record ScheduleTimeWithMemberInfoDTO(
        List<TimeTableDTO> timeTables,
        String username,
        String email,
        String type
) {
    public ScheduleTimeWithMemberInfoDTO(ScheduleTime st, String username, String email, String type) {
        this(
                st.getTimeTables().stream().map(TimeTableDTO::new).toList(),
                username,
                email,
                type
        );
    }
}

record TimeTableDTO(
        String time
) {
    public TimeTableDTO(TimeTable timeTable) {
        this(
                timeTable.getTime()
        );
    }
}
