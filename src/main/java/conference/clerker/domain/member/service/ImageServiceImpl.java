package conference.clerker.domain.member.service;

import conference.clerker.domain.member.DTO.ImageResponseDTO;
import conference.clerker.domain.member.DTO.ImageUploadDTO;
import conference.clerker.domain.member.repository.ImageRepository;
import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.member.schema.Member;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;

    @Value("${file.path}")
    private String uploadFolder;

    @Override
    public void upload(ImageUploadDTO imageUploadDTO, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("이메일이 존재하지 않습니다."));

        MultipartFile file = imageUploadDTO.getFile();
        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + file.getOriginalFilename();

        File destinationFile = new File(uploadFolder + imageFileName);

        try {
            file.transferTo(destinationFile);

            conference.clerker.domain.member.schema.Image image = imageRepository.findByMember(member);
            if (image != null) {
                // 이미지가 이미 존재하면 url 업데이트
                image.updateUrl("/profileImages/" + imageFileName);
            } else {
                // 이미지가 없으면 객체 생성 후 저장
                image = image.builder()
                        .member(member)
                        .url("/profileImages/" + imageFileName)
                        .build();
                imageRepository.save(image);
            }
        } catch (IOException e) {
            logger.error("파일 전송 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 전송 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public ImageResponseDTO findImage(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("이메일이 존재하지 않습니다."));
        conference.clerker.domain.member.schema.Image image = imageRepository.findByMember(member);

        String defaultImageUrl = "/profileImages/anonymous.png";

        if (image == null) {
            return ImageResponseDTO.builder()
                    .url(defaultImageUrl)
                    .build();
        } else {
            return ImageResponseDTO.builder()
                    .url(image.getUrl())
                    .build();
        }
    }
}