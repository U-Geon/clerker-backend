package conference.clerker.domain.organization.repository;

import conference.clerker.domain.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
