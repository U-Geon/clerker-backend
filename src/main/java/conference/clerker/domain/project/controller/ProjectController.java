package conference.clerker.domain.project.controller;


import conference.clerker.domain.project.entity.Project;
import conference.clerker.domain.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    @PutMapping("/create")
    @Operation(summary = "프로젝트 생성")
    public ResponseEntity<?> createProject() {
        projectService.createProject();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/create/{projectId}")
    @Operation(summary = "하위 프로젝트 생성", description = "Body 비어있어용")
        public ResponseEntity<?> createProject(@Parameter(required = true, description = "부모 프로젝트 ID")
                                  @PathVariable Long projectId) {
        projectService.createChildProject(projectId);
        return ResponseEntity.ok().build();
    }
}
