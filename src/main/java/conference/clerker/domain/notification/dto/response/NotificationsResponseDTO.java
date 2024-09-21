package conference.clerker.domain.notification.dto.response;

import conference.clerker.domain.notification.entity.Notification;
import java.time.LocalDateTime;

public record NotificationsResponseDTO(
        Long id,
        String content,
        LocalDateTime createdAt
) {
    public NotificationsResponseDTO(Notification notification) {
        this(
                notification.getId(),
                notification.getContent(),
                notification.getCreatedAt()
        );
    }
}
