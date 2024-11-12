package conference.clerker.domain.schedule.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record JoinScheduleRequestDTO(
        @NotEmpty
        List<String> timeTable
) {}
