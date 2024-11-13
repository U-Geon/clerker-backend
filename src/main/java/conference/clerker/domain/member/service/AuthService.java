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

        //구현 시작 기존 프로필 가져오기
        Profile existingprofile = (Profile) profileRepository.findByMember(member)
                .orElse(null);


        //username 만 업데이트 ( jpa 더티체킹으로?)
        member.setUsername(username);

        // 프로필 이미지가 업로드된 경우
        if (profileImage != null && !profileImage.isEmpty()) {
            // s3에서 삭제
            if (existingprofile != null) {
                s3FileService.deleteFile(profileImage.getOriginalFilename());
            }
            String filename = profileImage.getOriginalFilename();
            String profileURL = s3FileService.uploadFile("profile", filename, profileImage);

            // 기존 프로필 수정 or 새롭게 생성
            if (existingprofile != null) {
                existingprofile.setUrl(profileURL);
                existingprofile.setFilename(filename);
            } else {
                Profile newProfile = Profile.create(member, profileURL, filename);
                profileRepository.save(newProfile);
            }
        }


        // JPA dirty checking
        member.setUsername(username);

        String filename = profileImage.getOriginalFilename();
        String profileURL = s3FileService.uploadFile("profile", filename, profileImage);

        Profile profile = Profile.create(member, profileURL, filename);
        profileRepository.save(profile);
    }


}
