package conference.clerker.domain.meeting.service;


import conference.clerker.domain.meeting.dto.request.CreateMeetingRequestDTO;
import conference.clerker.domain.meeting.dto.response.FindMeetingsDTO;
import conference.clerker.domain.meeting.dto.response.MeetingFIleDTO;
import conference.clerker.domain.meeting.dto.response.MeetingResultDTO;
import conference.clerker.domain.meeting.repository.MeetingFileRepository;
import conference.clerker.domain.meeting.repository.MeetingRepository;
import conference.clerker.domain.meeting.schema.FileType;
import conference.clerker.domain.meeting.schema.Meeting;
import conference.clerker.domain.meeting.schema.MeetingFile;
import conference.clerker.domain.meeting.schema.Status;
import conference.clerker.domain.project.repository.ProjectRepository;
import conference.clerker.domain.project.schema.Project;
import conference.clerker.global.exception.CustomException;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.AuthException;
import conference.clerker.global.exception.domain.MeetingException;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    @Value("${baseUrl.front}")
    private String frontUrl;

    private final MeetingRepository meetingRepository;
    private final ProjectRepository projectRepository;
    private final GoogleMeetService googleMeetService;
    private final MeetingFileRepository meetingFileRepository;

    // 미팅 생성
    @Transactional
    public void create(Long projectId, CreateMeetingRequestDTO requestDTO) {

        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new AuthException(ErrorCode.PROJECT_NOT_FOUND));

        String googleMeetUrl = googleMeetService.createMeeting(requestDTO.name(), requestDTO.getStartTimeAsLocalTime());

        Meeting meeting = Meeting.create(project, requestDTO.name(), googleMeetUrl, requestDTO);
        meeting.setProject(project);
        project.getMeetings().add(meeting);
        meetingRepository.save(meeting);

    }

    // project ID를 통한 미팅 목록 조회
    public List<FindMeetingsDTO> findByProjectId(Long projectId) {
        return meetingRepository.findAllByProjectId(projectId).stream().map(FindMeetingsDTO::new).toList();
    }

    public Meeting findById(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingException(ErrorCode.MEETING_NOT_FOUND));
    }

    public Map<String, String> redirectToMeetingDetailPage(Long meetingId) {
        String redirectionUrl = frontUrl + "/project/summary/" + meetingId;

        Map<String, String> body = new HashMap<>();
        body.put("redirectUrl", redirectionUrl);

        return body;
    }

    // 미팅 파일 목록 조회
    public MeetingResultDTO findMeetingFiles(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new MeetingException(ErrorCode.MEETING_NOT_FOUND));

        if(meeting.getStatus() != Status.COMPLETE) throw new MeetingException(ErrorCode.MEETING_NOT_END);

        Map<FileType, MeetingFIleDTO> filesByType = meetingFileRepository.findByMeetingId(meetingId).stream()
                .filter(file -> file.getFileType() != FileType.IMAGE)
                .collect(Collectors.toMap(
                        MeetingFile::getFileType,
                        MeetingFIleDTO::new,
                        (existing, replacement) -> replacement // 충돌 발생 시, 최신 파일로 대체
                ));

        return new MeetingResultDTO(
                meeting.getId(),
                meeting.getName(),
                meeting.getDomain(),
                filesByType
        );
    }

    @Transactional
    public Meeting endMeeting(Status status, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingException(ErrorCode.MEETING_NOT_FOUND));
        meeting.setStatus(status);
        return meeting;
    }

    // 미팅을 PENDING 상태로 설정
    @Transactional
    public Meeting setMeetingPendingStatus(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingException(ErrorCode.MEETING_NOT_FOUND));
        meeting.setStatus(Status.PENDING);
        return meeting;
    }
}