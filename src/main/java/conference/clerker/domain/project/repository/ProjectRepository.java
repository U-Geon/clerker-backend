package conference.clerker.domain.project.repository;

import conference.clerker.domain.project.schema.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
