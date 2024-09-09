package conference.clerker.domain.project.service;

import conference.clerker.domain.project.dtos.request.UpdateProjectRequestDTO;
import conference.clerker.domain.project.entity.Project;
import conference.clerker.domain.project.repository.ProjectRepository;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.ProjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    // 프로젝트 생성
    @Transactional
    public Long createProject() {
        Project project = Project.create();
        projectRepository.save(project);
        return project.getId();
    }

    // 하위 프로젝트 생성
    @Transactional
    public Long createChildProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ErrorCode.PROJECT_NOT_FOUND));
        Project child = Project.create(project);
        projectRepository.save(child);
        return child.getId();
    }

    // 프로젝트 삭제
    @Transactional
    public Boolean deleteById(Long projectId) {
        try {
            projectRepository.deleteById(projectId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 프로젝트명 업데이트
    @Transactional
    public void update(Long projectId, UpdateProjectRequestDTO requestDTO) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ErrorCode.PROJECT_NOT_FOUND));
        project.setName(requestDTO.getProjectName());
    }
}
