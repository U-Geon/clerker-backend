package conference.clerker.domain.notification.controller;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.notification.dto.response.NotificationsResponseDTO;
import conference.clerker.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notify")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 목록")
    public ResponseEntity<List<NotificationsResponseDTO>> findAll(
            @AuthenticationPrincipal Member member) {
        return ResponseEntity.ok().body(notificationService.findAllByMemberId(member.getId()));
    }
}
