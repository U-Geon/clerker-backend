package conference.clerker.domain.notification.dtos.response;

import conference.clerker.domain.notification.entity.Notification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class NotificationsResponseDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public NotificationsResponseDTO(Notification notification) {
        this.id = notification.getId();
        this.content = notification.getContent();
        this.createdAt = notification.getCreatedAt();
    }
}
