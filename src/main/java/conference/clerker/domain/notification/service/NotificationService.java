package conference.clerker.domain.notification.service;

import conference.clerker.domain.member.entity.Member;
import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.notification.dtos.response.NotificationsResponseDTO;
import conference.clerker.domain.notification.entity.Notification;
import conference.clerker.domain.notification.repository.NotificationRepository;
import conference.clerker.domain.project.entity.Project;
import conference.clerker.domain.project.repository.ProjectRepository;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    // 알림 생성 (notificationType에 스케쥴 조율인지, 회의 일정인지 받기.)
    @Transactional
    public void notify(Long memberId, Long projectId, String projectName, LocalDate startDate, LocalDate endDate, String notificationType) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AuthException(ErrorCode.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new AuthException(ErrorCode.PROJECT_NOT_FOUND));

        String s = projectName + "이(가) " + startDate + " ~ " + endDate + "까지의 " + notificationType + "이(가) 있습니다.";

        Notification notification = Notification.create(member, project, s);
        notificationRepository.save(notification);
    }

    // 알림 조회
    public List<NotificationsResponseDTO> findAllByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AuthException(ErrorCode.MEMBER_NOT_FOUND));
        return notificationRepository.findByMember(member)
                .stream().map(NotificationsResponseDTO::new).toList();
    }
}
