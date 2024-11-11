package conference.clerker.domain.project.controller;

import conference.clerker.domain.notification.service.NotificationService;
import conference.clerker.domain.organization.dto.ProjectInfoDTO;
import conference.clerker.domain.organization.service.OrganizationService;
import conference.clerker.domain.project.dto.request.InviteMembersRequestDTO;
import conference.clerker.domain.project.dto.request.UpdateProjectRequestDTO;
import conference.clerker.domain.project.dto.response.ProjectWithMeetingsDTO;
import conference.clerker.domain.project.service.ProjectService;
import conference.clerker.domain.schedule.service.ScheduleService;
import conference.clerker.global.aop.roleCheck.RoleCheck;
import conference.clerker.global.oauth2.service.OAuth2UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;
    private final OrganizationService organizationService;
    private final ScheduleService scheduleService;
    private final NotificationService notificationService;

    @PostMapping("/create")
    @Operation(summary = "프로젝트 생성", description = "프로젝트 생성 탭 클릭 시 요청. 토큰 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "요청에 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "PROJECT-001", description = "프로젝트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "AUTH-001", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> createProject(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        Long projectId = projectService.createProject();
        organizationService.createOwner(principal.getMember().getId(), projectId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectID}/create-child")
    @Operation(summary = "하위 프로젝트 생성", description = "하위 프로젝트 생성 탭 클릭 시 요청. 토큰 필요")
    public ResponseEntity<Void> createProject(
            @Parameter(required = true, description = "부모 프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @AuthenticationPrincipal OAuth2UserPrincipal principal) {
        Long childProjectId = projectService.createChildProject(projectId);
        organizationService.createOwner(principal.getMember().getId(), childProjectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "프로젝트 목록 조회", description = "특정 회원의 전체적인 프로젝트 목록을 조회하는 API. 토큰 정보 필요")
    public ResponseEntity<List<ProjectWithMeetingsDTO>> getProjects(
            @AuthenticationPrincipal OAuth2UserPrincipal principal) {
        return ResponseEntity.ok().body(organizationService.findProjectByMember(principal.getMember().getId()));
    }

    @GetMapping("/{projectID}/info")
    @Operation(summary = "프로젝트 정보 (이름 + 멤버 정보) 조회 API", description = "프로젝트 이름 및 프로젝트 내 회원들 목록을 조회하는 API.")
    public ResponseEntity<ProjectInfoDTO> getProject(
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        return ResponseEntity.ok().body(organizationService.findProject(projectId));
    }

    // 프로젝트 삭제
    @RoleCheck(role = "OWNER")
    @DeleteMapping("/{projectID}")
    @Operation(summary = "프로젝트 삭제", description = "프로젝트 삭제 API")
    public ResponseEntity<String> deleteProject(
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        scheduleService.deleteAllScheduleByProjectId(projectId);
        notificationService.deleteAllByProjectId(projectId);
        Boolean isDeletedProject = projectService.deleteById(projectId);
        if(isDeletedProject) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.internalServerError().body("{\"msg\" : \"project delete failed\"}");
        }
    }

    // 프로젝트 이름 및 멤버 정보 수정
    @RoleCheck(role = "OWNER")
    @PatchMapping("/{projectID}")
    @Operation(summary = "프로젝트 정보 (이름 + 멤버 정보) 수정 API", description = " 수정 API")
    public ResponseEntity<String> updateName(
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @RequestBody UpdateProjectRequestDTO updateProjectRequestDTO) {
        projectService.update(projectId, updateProjectRequestDTO);
        organizationService.updateMembers(updateProjectRequestDTO);
        return ResponseEntity.ok().body("{\"msg\" : \"success\"}");
    }

    // 회원 초대 API
    @RoleCheck(role = "OWNER")
    @PostMapping("/{projectID}/join")
    @Operation(summary = "회원 초대", description = "email List를 받아서 멤버 초대. List가 비어 있으면 에러")
    public ResponseEntity<String> invite(
            @RequestBody InviteMembersRequestDTO inviteMembersRequestDTO,
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        organizationService.inviteMember(inviteMembersRequestDTO.emails(), projectId);
        return ResponseEntity.ok().body("{\"msg\" : \"success\"}");
    }

    // 프로젝트 나가기
    @RoleCheck(role = "MEMBER")
    @DeleteMapping("/{projectID}/out")
    @Operation(summary = "프로젝트 나가기", description = "방장만 내보낼 수 있음.")
    public ResponseEntity<Void> outOfProject(
            @AuthenticationPrincipal OAuth2UserPrincipal principal,
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        organizationService.outOfProject(principal.getMember().getId(), projectId);
        return ResponseEntity.noContent().build();
    }

}
