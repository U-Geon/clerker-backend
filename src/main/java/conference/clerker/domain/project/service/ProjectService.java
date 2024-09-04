package conference.clerker.domain.project.service;

import com.amazonaws.services.kms.model.NotFoundException;
import conference.clerker.domain.project.entity.Project;
import conference.clerker.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public void createProject() {
        Project project = Project.create();
        projectRepository.save(project);
    }

    @Transactional
    public void createChildProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(IllegalArgumentException::new);
        Project child = Project.create(project);
        projectRepository.save(child);
    }
}
