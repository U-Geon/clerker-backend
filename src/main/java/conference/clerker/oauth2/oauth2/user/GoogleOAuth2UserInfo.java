package conference.clerker.oauth2.oauth2.user;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final String AccessToken;
    private final String email;
    private final String name;
    private final String nickName;

    public GoogleOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        this.attributes = attributes;
        this.email = (String) attributes.get("email");
        this.name = (String) attributes.get("name");
        this.nickName = (String) attributes.get("nickname");
        this.AccessToken = accessToken;
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.GOOGLE;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getAccessToken() {
        return AccessToken;
    }
    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNickname() {
        return nickName;
    }

}
