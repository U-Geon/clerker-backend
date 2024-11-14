package conference.clerker.domain.project.dto.response;

import conference.clerker.domain.meeting.schema.Meeting;
import conference.clerker.domain.meeting.schema.Status;
import conference.clerker.domain.project.schema.Project;

import java.util.List;

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
                        .filter(meeting -> meeting.getStatus().equals(Status.COMPLETE))
                        .map(MeetingDTO::new)
                        .toList()
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
