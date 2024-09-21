package conference.clerker.domain.notification.repository;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMember(Member member);
}
