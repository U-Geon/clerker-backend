package conference.clerker.domain.notification.repository;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMember(Member member);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") Long projectId);
}
