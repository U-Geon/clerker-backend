package conference.clerker.domain.organization.service;


import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.organization.dto.MemberInfoDTO;
import conference.clerker.domain.organization.dto.ProjectInfoDTO;
import conference.clerker.domain.organization.schema.Organization;
import conference.clerker.domain.organization.schema.Role;
import conference.clerker.domain.organization.repository.OrganizationRepository;
import conference.clerker.domain.project.dto.request.UpdateProjectRequestDTO;
import conference.clerker.domain.project.entity.Project;
import conference.clerker.domain.project.repository.ProjectRepository;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.AuthException;
import conference.clerker.global.exception.domain.OrganizationException;
import conference.clerker.global.exception.domain.ProjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    // 프로젝트 owner 생성
    @Transactional
    public void createOwner(Long memberId, Long projectId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AuthException(ErrorCode.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ErrorCode.PROJECT_NOT_FOUND));
        Organization owner = Organization.createOwner(member, project);
        organizationRepository.save(owner);
    }

    // 멤버 초대
    @Transactional
    public void inviteMember(List<String> emails, Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ErrorCode.PROJECT_NOT_FOUND));

        for (String email : emails) {
            Member member = memberRepository.findByEmail(email).orElseThrow(() -> new AuthException(ErrorCode.MEMBER_NOT_FOUND));
            boolean isMemberAlreadyInProject = organizationRepository
                    .findByMemberIdAndProjectId(member.getId(), projectId)
                    .isPresent();

            if (isMemberAlreadyInProject) {
                throw new OrganizationException(ErrorCode.DUPLICATED_ORGANIZATION);
            }
            Organization organizationMember = Organization.createMember(member, project);
            organizationRepository.save(organizationMember);
        }
    }

    // 특정 회원의 전체 프로젝트 목록
    public List<Project> findProjectByMember(Long memberId) {
        return organizationRepository.findProjectsByMemberId(memberId);
    }

    // 프로젝트 이름 + 소속 멤버들 정보
    public ProjectInfoDTO findProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ErrorCode.PROJECT_NOT_FOUND));
        List<MemberInfoDTO> membersByProjectId = organizationRepository.findMemberInfosByProjectId(projectId);
        return new ProjectInfoDTO(project.getName(), membersByProjectId);
    }

    // 프로젝트 내 멤버들 조회
    public List<Member> findMembersByProjectId(Long projectId) {
        return organizationRepository.findMembersByProjectId(projectId);
    }

    // 프로젝트 내 멤버들 삭제
    @Transactional
    public Boolean deleteMembers(Long projectId) {
        try {
            organizationRepository.deleteAllByProjectId(projectId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 멤버들 정보 수정
    @Transactional
    public void updateMembers(UpdateProjectRequestDTO requestDTO) {
        requestDTO.members().forEach(member -> {
            Organization organization = organizationRepository.findById(member.organizationId()).orElseThrow(() -> new OrganizationException(ErrorCode.ORGANIZATION_NOT_FOUND));
            organization.setRole(member.role());
            organization.setType(member.type());
        });
    }

    // 프로젝트 나가기
    @Transactional
    public void outOfProject(Long memberId, Long projectId) {
        Organization organization = organizationRepository.findByMemberIdAndProjectId(memberId, projectId).orElseThrow(() -> new OrganizationException(ErrorCode.ORGANIZATION_NOT_FOUND));
        organizationRepository.deleteById(organization.getId());
    }

    // role getter
    public Role findRoleByMemberAndProject(Long memberId, Long projectId) {
        Organization organization = organizationRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new OrganizationException(ErrorCode.ORGANIZATION_NOT_FOUND));
        return organization.getRole();
    }
}
