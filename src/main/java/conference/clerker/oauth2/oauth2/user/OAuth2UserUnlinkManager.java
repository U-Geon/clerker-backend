package conference.clerker.oauth2.oauth2.user;

import conference.clerker.oauth2.oauth2.exception.OAuth2AuthenticationProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2UserUnlinkManager {
    private final GoogleOAuth2Userlink googleOAuth2Userlink;

    public void unlink(OAuth2Provider provider, String accessToken) {
        if (OAuth2Provider.GOOGLE.equals(provider)) {
            googleOAuth2Userlink.unlink(accessToken);
        } else {
            throw new OAuth2AuthenticationProcessingException(
                    "Unlink with " + provider.getRegistrationId() + " is not supported");
        }
    }
}