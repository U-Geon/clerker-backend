package conference.clerker.domain.meeting.service;


import conference.clerker.domain.meeting.dto.response.FindMeetingsDTO;
import conference.clerker.domain.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private final MeetingRepository meetingRepository;

    // project ID를 통한 미팅 목록 조회
    public List<FindMeetingsDTO> findByProjectId(Long projectId) {
        return meetingRepository.findAllByProjectId(projectId).stream().map(FindMeetingsDTO::new).toList();
    }
}
