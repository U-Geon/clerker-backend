package conference.clerker.global.oauth2.service;

import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.member.schema.Member;
import conference.clerker.global.oauth2.exception.OAuth2AuthenticationProcessingException;
import conference.clerker.global.oauth2.user.GoogleOAuth2UserInfo;
import conference.clerker.global.oauth2.user.OAuth2UserInfoFactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {


        log.info("메서드 실행 : loadUser");
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {

            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId();

        GoogleOAuth2UserInfo oAuth2UserInfo = (GoogleOAuth2UserInfo) OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        // OAuth2UserInfo field value validation
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Member member = createMember(oAuth2UserInfo.getEmail(), oAuth2UserInfo.getName());

        return new OAuth2UserPrincipal(oAuth2UserInfo, member);
    }

    private Member createMember(String email, String username) {
        Member member = memberRepository.findByEmail(email).orElse(Member.create(username, email));
        return memberRepository.save(member);
    }
}
