package conference.clerker.domain.project.controller;


import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.organization.dto.ProjectInfoDTO;
import conference.clerker.domain.organization.service.OrganizationService;
import conference.clerker.domain.project.dto.request.InviteMembersRequestDTO;
import conference.clerker.domain.project.dto.request.UpdateProjectRequestDTO;
import conference.clerker.domain.project.entity.Project;
import conference.clerker.domain.project.service.ProjectService;
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

    @PostMapping("/create")
    @Operation(summary = "프로젝트 생성", description = "프로젝트 생성 탭 클릭 시 요청. 토큰 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "요청에 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "PROJECT-001", description = "프로젝트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "AUTH-001", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> createProject(@AuthenticationPrincipal Member member) {
        Long id = projectService.createProject();
        organizationService.createOwner(member.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create/{projectID}")
    @Operation(summary = "하위 프로젝트 생성", description = "하위 프로젝트 생성 탭 클릭 시 요청. 토큰 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "PROJECT-001", description = "프로젝트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "AUTH-001", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
    })
        public ResponseEntity<Void> createProject(
                @Parameter(required = true, description = "부모 프로젝트 ID", in = ParameterIn.PATH)
                @PathVariable("projectID") Long projectId,
                @AuthenticationPrincipal Member member) {
        Long childProjectId = projectService.createChildProject(projectId);
        organizationService.createOwner(member.getId(), childProjectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "프로젝트 목록 조회", description = "특정 회원의 전체적인 프로젝트 목록을 조회하는 API. 토큰 정보 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "PROJECT-001", description = "프로젝트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "AUTH-001", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<List<Project>> getProjects(
            @AuthenticationPrincipal Member member) {
        return ResponseEntity.ok().body(organizationService.findProjectByMember(member.getId()));
    }

    @GetMapping("/{projectID}")
    @Operation(summary = "프로젝트 이름 + 프로젝트 내 회원 정보 API", description = "프로젝트 이름 및 프로젝트 내 회원들 목록을 조회하는 API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "PROJECT-001", description = "프로젝트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<ProjectInfoDTO> getProject(
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        return ResponseEntity.ok().body(organizationService.findProject(projectId));
    }

    // 프로젝트 삭제
    @DeleteMapping("/{projectID}")
    @Operation(summary = "프로젝트 삭제", description = "프로젝트 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> delete(
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        Boolean isDeletedProject = projectService.deleteById(projectId);
        Boolean isDeletedMembers = organizationService.deleteMembers(projectId);
        if(isDeletedProject && isDeletedMembers) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 프로젝트 이름 및 멤버 정보 수정
    @PatchMapping("/{projectID}")
    @Operation(summary = "프로젝트명 수정", description = "프로젝트명 수정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "PROJECT-001", description = "프로젝트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<String> updateName(
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @RequestBody UpdateProjectRequestDTO updateProjectRequestDTO) {
        projectService.update(projectId, updateProjectRequestDTO);
        organizationService.updateMembers(updateProjectRequestDTO);
        return ResponseEntity.ok().body("{\"msg\" : \"success\"}");
    }

    // 회원 초대 API
    @PostMapping("/join/{projectID}")
    @Operation(summary = "회원 초대", description = "email List를 받아 프로젝트 내에 멤버를 초대하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "PROJECT-001", description = "프로젝트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "AUTH-001", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<String> invite(
            @RequestBody InviteMembersRequestDTO inviteMembersRequestDTO,
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        organizationService.inviteMember(inviteMembersRequestDTO.emails(), projectId);
        return ResponseEntity.ok().body("{\"msg\" : \"success\"}");
    }

    // 프로젝트 나가기
    @DeleteMapping("/out/{projectID}")
    @Operation(summary = "프로젝트 나가기", description = "방장만 내보낼 수 있음.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "성공", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> outOfProject(
            @AuthenticationPrincipal Member member,
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        organizationService.outOfProject(member.getId(), projectId);
        return ResponseEntity.noContent().build();
    }

}