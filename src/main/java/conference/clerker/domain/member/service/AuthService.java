package conference.clerker.domain.member.service;


import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.member.repository.ProfileRepository;
import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.member.schema.Profile;
import conference.clerker.global.aws.s3.S3FileService;
import conference.clerker.global.exception.domain.AuthException;
import conference.clerker.global.jwt.JwtProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static conference.clerker.global.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final S3FileService s3FileService;
    private final JwtProvider jwtProvider;

    @Transactional
    public void update(Long memberId, @Valid MultipartFile profileImage, @Valid String username) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AuthException(MEMBER_NOT_FOUND));

        // JPA dirty checking
        member.setUsername(username);

        String filename = profileImage.getOriginalFilename();
        String profileURL = s3FileService.uploadFile("profile", filename, profileImage);

        Profile profile = Profile.create(member, profileURL, filename);
        profileRepository.save(profile);
    }


}
