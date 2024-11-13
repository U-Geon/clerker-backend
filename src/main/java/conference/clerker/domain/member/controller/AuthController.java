package conference.clerker.domain.member.controller;


import conference.clerker.domain.member.service.AuthService;
import conference.clerker.global.oauth2.service.OAuth2UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 수정 API", description = "프로필 사진 업데이트")
    public ResponseEntity<Void> update(
            @AuthenticationPrincipal OAuth2UserPrincipal principal,
            @Parameter(description = "프로필 사진", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("profileImage") MultipartFile profileImage,
            @Parameter(description = "변경하려는 username", required = true)
            @Valid @RequestPart("username") String username) {
        String accessToken = authService.update(principal.getMember().getId(), profileImage, username);
        if(accessToken == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.noContent().header("Authorization", "Bearer " + accessToken).build();
    }

}
