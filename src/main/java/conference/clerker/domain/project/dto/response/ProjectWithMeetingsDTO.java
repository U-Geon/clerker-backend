package conference.clerker.domain.project.dto.response;

import conference.clerker.domain.meeting.schema.Meeting;
import conference.clerker.domain.project.schema.Project;

import java.util.List;
import java.util.stream.Collectors;

public record ProjectWithMeetingsDTO(
        Long projectId,
        String name,
        List<Project> childProjects,
        List<MeetingDTO> meetings
) {
    public ProjectWithMeetingsDTO(Project project) {
        this(
                project.getId(),
                project.getName(),
                project.getChildProjects(),
                project.getMeetings().stream()
                        .filter(Meeting::getIsEnded)
                        .map(MeetingDTO::new)
                        .collect(Collectors.toList())
        );
    }

}

record MeetingDTO(
        Long meetingId,
        String name
) {
    public MeetingDTO(Meeting meeting) {
        this(
                meeting.getId(),
                meeting.getName()
        );
    }
}
