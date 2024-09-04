package conference.clerker.domain.meeting.controller;

import conference.clerker.domain.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class MeetingController {

    private final MeetingService meetingService;
}
