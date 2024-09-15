package conference.clerker.domain.schedule.dtos.response;

import conference.clerker.domain.organization.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScheduleTimeWithMemberInfoDTO {
    private List<String> timeTable;
    private String username;
    private String email;
    private String phoneNumber;
    private Role role;

    public ScheduleTimeWithMemberInfoDTO(Object[] objects) {

    }
}