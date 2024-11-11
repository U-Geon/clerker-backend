package conference.clerker.global.jwt;

import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.member.schema.Member;
import conference.clerker.global.oauth2.service.OAuth2UserPrincipal;
import conference.clerker.global.oauth2.user.GoogleOAuth2UserInfo;
import conference.clerker.global.oauth2.util.CustomOAuth2AuthenticationToken;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwt != null && jwtProvider.validateToken(jwt)) {
                String email = jwtProvider.getEmailFromToken(jwt);

                Authentication authentication = getOAuth2Authentication(email);
                // 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Authentication getOAuth2Authentication(String email) {

        // Member 객체 로드
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No member found with email: " + email));

        // GoogleOAuth2UserInfo 생성
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", member.getUsername());
        // 필요한 추가 속성 설정
        GoogleOAuth2UserInfo googleOAuth2UserInfo = new GoogleOAuth2UserInfo(attributes);

        // OAuth2UserPrincipal 생성
        OAuth2UserPrincipal oauth2User = new OAuth2UserPrincipal(googleOAuth2UserInfo, member);

        // Authentication 객체 생성
        return new CustomOAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "google");
    }
}