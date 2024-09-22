package conference.clerker.oauth2.oauth2.handler;

import conference.clerker.oauth2.oauth2.HttpCookieOauth2AuthorizationRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import conference.clerker.oauth2.oauth2.util.CookieUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.Cookie;

import static conference.clerker.oauth2.oauth2.HttpCookieOauth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HttpCookieOauth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {


        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("/");

        // 예외 메세지 url 인코딩
        String errorMessage = URLEncoder.encode(exception.getLocalizedMessage(), StandardCharsets.UTF_8);

        // 인코딩된 메세지를 queryParam 에 추가 - 건이형이 알려줌
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", errorMessage)
                .build().toUriString();

        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}