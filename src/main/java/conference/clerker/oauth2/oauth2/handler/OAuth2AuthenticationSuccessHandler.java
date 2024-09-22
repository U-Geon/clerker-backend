package conference.clerker.oauth2.oauth2.handler;

import conference.clerker.oauth2.oauth2.HttpCookieOauth2AuthorizationRequestRepository;
import conference.clerker.oauth2.oauth2.service.OAuth2UserPrincipal;
import conference.clerker.oauth2.oauth2.user.OAuth2Provider;
import conference.clerker.oauth2.oauth2.user.OAuth2UserUnlinkManager;
import conference.clerker.oauth2.oauth2.util.CookieUtils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Optional;

import static conference.clerker.oauth2.oauth2.HttpCookieOauth2AuthorizationRequestRepository.MODE_PARAM_COOKIE_NAME;
import static conference.clerker.oauth2.oauth2.HttpCookieOauth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;


@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private final HttpCookieOauth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    @Autowired
    private final OAuth2UserUnlinkManager oAuth2UserUnlinkManager;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String targetUrl = "http://localhost:3000/login/callback";

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String mode = CookieUtils.getCookie(request, MODE_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("");

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);

        if (principal == null) {
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("error", "Login failed")
                    .build().toUriString();
        }

        if ("login".equalsIgnoreCase(mode)) {
            return handleLogin(principal, targetUrl);
        } else if ("unlink".equalsIgnoreCase(mode)) {
            return handleUnlink(principal, targetUrl);
        }

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", "Login failed")
                .build().toUriString();
    }

    private String handleLogin(OAuth2UserPrincipal principal, String targetUrl) {
        log.info("email={}, name={}, nickname={}, accessToken={}",
                principal.getUserInfo().getEmail(),
                principal.getUserInfo().getName(),
                principal.getUserInfo().getNickname(),
                principal.getUserInfo().getAccessToken());

        String accessToken = "test_access_token";  // TODO: 실제 액세스 토큰 생성 로직
        String refreshToken = "test_refresh_token";  // TODO: 실제 리프레시 토큰 생성 로직

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("access_token", accessToken)
                .queryParam("refresh_token", refreshToken)
                .build().toUriString();
    }

    private String handleUnlink(OAuth2UserPrincipal principal, String targetUrl) {
        String accessToken = principal.getUserInfo().getAccessToken();
        OAuth2Provider provider = principal.getUserInfo().getProvider();

        oAuth2UserUnlinkManager.unlink(provider, accessToken);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .build().toUriString();
    }

    private OAuth2UserPrincipal getOAuth2UserPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2UserPrincipal) {
            return (OAuth2UserPrincipal) principal;
        }
        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}