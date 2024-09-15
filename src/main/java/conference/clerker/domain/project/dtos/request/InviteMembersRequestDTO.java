package conference.clerker.domain.project.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class InviteMembersRequestDTO {
    @NotEmpty
    private List<String> emails;
}
