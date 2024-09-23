package conference.clerker.global.oauth2.service;

import conference.clerker.domain.member.schema.Member;
import conference.clerker.global.oauth2.user.GoogleOAuth2UserInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter @Setter
public class OAuth2UserPrincipal implements OAuth2User, UserDetails {

    private GoogleOAuth2UserInfo googleOAuth2UserInfo;
    private Member member;

    public OAuth2UserPrincipal(GoogleOAuth2UserInfo googleOAuth2UserInfo, Member member) {
        this.googleOAuth2UserInfo = googleOAuth2UserInfo;
        this.member = member;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return googleOAuth2UserInfo.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return googleOAuth2UserInfo.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return googleOAuth2UserInfo.getEmail();
    }

}