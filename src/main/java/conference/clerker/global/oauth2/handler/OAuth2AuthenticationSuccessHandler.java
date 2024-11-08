package conference.clerker.global.oauth2.handler;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.global.jwt.JwtProvider;
import conference.clerker.global.oauth2.service.OAuth2UserPrincipal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = "http://localhost:3000/login/callback";

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        log.info("success handler 실행");

        String uriString = getRedirectionURL(authentication, targetUrl);

        response.sendRedirect(uriString);
    }

    private String getRedirectionURL(Authentication authentication, String targetUrl) {
        log.info("getRedirectionURL 실행");
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        Member member = principal.getMember();

        String username = member.getUsername();
        String email = member.getEmail();

        log.info("username, email : {} {}", username, email);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("email", email);

        String accessToken = jwtProvider.generateToken(claims);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("name", username)
                .queryParam("email", email)
                .queryParam("token", accessToken)
                .build().toUriString();
    }
}