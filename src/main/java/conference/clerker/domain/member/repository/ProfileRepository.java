package conference.clerker.domain.member.repository;

import conference.clerker.domain.member.schema.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
