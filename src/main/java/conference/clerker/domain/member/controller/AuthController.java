package conference.clerker.domain.member.controller;


import conference.clerker.domain.member.dto.response.ProfileModifyResponseDTO;
import conference.clerker.domain.member.schema.Profile;
import conference.clerker.domain.member.service.AuthService;
import conference.clerker.global.oauth2.service.OAuth2UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
    public ResponseEntity<ProfileModifyResponseDTO> update(
            @AuthenticationPrincipal OAuth2UserPrincipal principal,
            @Parameter(description = "프로필 사진", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("profileImage") MultipartFile profileImage,
            @Parameter(description = "변경하려는 username", required = true)
            @RequestPart("username") String username) {
        return ResponseEntity.ok().body(authService.update(principal.getMember().getId(), profileImage, username));
    }

    @GetMapping("/profile")
    @Operation(summary = "사용자 프로필 url 조회")
    public ResponseEntity<Profile> getProfileUrl(
            @AuthenticationPrincipal OAuth2UserPrincipal principal
    ) {
        return ResponseEntity.ok().body(authService.findProfile(principal.getMember().getId()));
    }

}
