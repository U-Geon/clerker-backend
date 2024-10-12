package conference.clerker.domain.meeting.service;


import conference.clerker.domain.meeting.dto.request.CreateMeetingRequestDTO;
import conference.clerker.domain.meeting.dto.response.FindMeetingsDTO;
import conference.clerker.domain.meeting.repository.MeetingRepository;
import conference.clerker.domain.meeting.schema.Meeting;
import conference.clerker.domain.project.repository.ProjectRepository;
import conference.clerker.domain.project.schema.Project;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ProjectRepository projectRepository;
    private final GoogleMeetService googleMeetService;

    // 미팅 생성
    @Transactional
    public void create(Long projectId, CreateMeetingRequestDTO requestDTO) {
        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new AuthException(ErrorCode.PROJECT_NOT_FOUND));

        String googlemeetUrl = googleMeetService.createMeeting(requestDTO.name(), requestDTO.startDate());

        Meeting meeting = Meeting.create(project, requestDTO.name(), googlemeetUrl, requestDTO);
        meetingRepository.save(meeting);
    }

    // project ID를 통한 미팅 목록 조회
    public List<FindMeetingsDTO> findByProjectId(Long projectId) {
        return meetingRepository.findAllByProjectId(projectId).stream().map(FindMeetingsDTO::new).toList();
    }
}