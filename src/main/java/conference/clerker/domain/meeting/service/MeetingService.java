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
import conference.clerker.domain.project.repository.ProjectRepository;
import conference.clerker.domain.project.schema.Project;
import conference.clerker.global.exception.CustomException;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.AuthException;
import conference.clerker.global.exception.domain.MeetingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ProjectRepository projectRepository;
    private final GoogleMeetService googleMeetService;
    private final MeetingFileRepository meetingFileRepository;

    // 미팅 생성
    @Transactional
    public void create(Long projectId, CreateMeetingRequestDTO requestDTO) {
        try {
            Project project = projectRepository.findById(projectId).orElseThrow(()
                    -> new AuthException(ErrorCode.PROJECT_NOT_FOUND));

            String googleMeetUrl = googleMeetService.createMeeting(requestDTO.name(), requestDTO.startDateTime());

            Meeting meeting = Meeting.create(project, requestDTO.name(), googleMeetUrl, requestDTO);
            meeting.setProject(project);
            project.getMeetings().add(meeting);
            meetingRepository.save(meeting);
        } catch (NullPointerException e) {
            throw new CustomException(ErrorCode.BODY_VALUE_NOT_FOUND);
        }
    }

    // project ID를 통한 미팅 목록 조회
    public List<FindMeetingsDTO> findByProjectId(Long projectId) {
        return meetingRepository.findAllByProjectId(projectId).stream().map(FindMeetingsDTO::new).toList();
    }

    public Meeting findById(Long id) {
        return meetingRepository.findById(id).orElseThrow(() -> new MeetingException(ErrorCode.MEETING_NOT_FOUND));
    }

    public MeetingResultDTO findByIdAndMeetingFileId(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found id: " + meetingId));

        Map<FileType, List<MeetingFIleDTO>> filesByType = meetingFileRepository.findByMeetingId(meetingId).stream()
                .filter(file -> file.getFileType() != FileType.IMAGE) // IMAGE 타입을 제외
                .collect(Collectors.groupingBy(
                        MeetingFile::getFileType,
                        Collectors.mapping(MeetingFIleDTO::new, Collectors.toList())
                ));



        return new MeetingResultDTO(
                meeting.getId(),
                meeting.getName(),
                meeting.getDomain(),
                filesByType
        );
    }
}