package conference.clerker.domain.schedule.dtos.request;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class JoinScheduleRequestDTO {
    private List<String> timeTable;
}
