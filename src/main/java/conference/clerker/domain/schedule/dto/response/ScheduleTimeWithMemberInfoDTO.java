package conference.clerker.domain.schedule.dto.response;

import conference.clerker.domain.organization.schema.Role;
import conference.clerker.domain.schedule.schema.ScheduleTime;
import conference.clerker.domain.schedule.schema.TimeTable;

import java.util.List;

public record ScheduleTimeWithMemberInfoDTO(
        List<TimeTableDTO> timeTables,
        String username,
        String email,
        String type,
        String role
) {
    public ScheduleTimeWithMemberInfoDTO(ScheduleTime st, String username, String email, String type, Role role) {
        this(
                st.getTimeTables().stream().map(TimeTableDTO::new).toList(),
                username,
                email,
                type,
                role.toString()
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
