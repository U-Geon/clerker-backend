package conference.clerker.global.oauth2.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;

public class CustomOAuth2AuthenticationToken extends OAuth2AuthenticationToken {

    public CustomOAuth2AuthenticationToken(OAuth2User principal, Collection<? extends GrantedAuthority> authorities, String authorizedClientRegistrationId) {
        super(principal, authorities, authorizedClientRegistrationId);
    }
}
