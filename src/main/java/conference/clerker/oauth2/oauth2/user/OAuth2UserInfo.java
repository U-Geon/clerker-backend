package conference.clerker.oauth2.oauth2.user;

import java.util.Map;

public interface OAuth2UserInfo {
    OAuth2Provider getProvider();

    Map<String, Object> getAttributes();

    String getAccessToken();

    String getEmail();

    String getName();

    String getNickname();

}
