package conference.clerker.domain.member.repository;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.member.schema.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Object> findByMember(Member member);
}
