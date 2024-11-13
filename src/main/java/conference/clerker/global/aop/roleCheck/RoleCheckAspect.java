package conference.clerker.global.aop.roleCheck;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.organization.service.OrganizationService;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.OrganizationException;
import conference.clerker.global.oauth2.service.OAuth2UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final OrganizationService organizationService;

    @Before("@annotation(conference.clerker.global.aop.roleCheck.RoleCheck)") // 커스텀 애노테이션이 붙은 메서드 전에 실행
    public void checkRole(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RoleCheck roleCheck = method.getAnnotation(RoleCheck.class);
        String requiredRole = roleCheck.role();

        // 메서드의 인자에서 @PathVariable 값 추출
        Object[] args = joinPoint.getArgs();
        Long projectId = null;

        for (Object arg : args) {
            if (arg instanceof Long) {
                projectId = (Long) arg;
                break;
            }
        }

        if (projectId == null) {
            throw new IllegalArgumentException("URL에서 Project ID를 찾을 수 없습니다");
        }

        // 인증된 사용자 (Member) 가져오기
        OAuth2UserPrincipal oAuth2UserPrincipal = (OAuth2UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Organization에서 Role 정보 조회
        String role = organizationService.findRoleByMemberAndProject(oAuth2UserPrincipal.getMember().getId(), projectId).toString();

        // Role 체크
        if (!role.equals(requiredRole)) {
            throw new OrganizationException(ErrorCode.FORBIDDEN_MEMBER);
        }
    }
}
