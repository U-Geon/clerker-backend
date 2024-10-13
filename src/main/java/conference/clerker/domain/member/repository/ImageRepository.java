package conference.clerker.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import conference.clerker.domain.member.schema.Image;
import conference.clerker.domain.member.schema.Member;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByMember(Member member);
}