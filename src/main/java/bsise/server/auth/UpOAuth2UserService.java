package bsise.server.auth;

import bsise.server.user.User;
import bsise.server.user.UserRepository;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UpOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // registrationId 확인
        log.debug("client registration={}", userRequest.getClientRegistration());
        log.debug("access token={}", userRequest.getAccessToken().getTokenValue());
        log.debug("attributes={}", super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = extractOAuth2UserInfo(userRequest, oAuth2User);

        Optional<User> optionalUser = userRepository.findByUsername(oAuth2UserInfo.getUserName());

        // 신규 OAuth2 유저 => 저장
        if (optionalUser.isEmpty()) {
            User newUser = userRepository.save(User.makeFromOAuth2UserInfo(oAuth2UserInfo));
            log.debug("--- 신규 OAuth2 유저: {} ---", newUser.getUsername());
            return new UpUserDetails(newUser, oAuth2User.getAttributes());
        }

        // 기존 유저 => UserDetails 반환
        log.debug("--- 기존 OAuth2 유저 ---");
        return new UpUserDetails(optionalUser.get(), oAuth2User.getAttributes());
    }

    private OAuth2UserInfo extractOAuth2UserInfo(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (registrationId.equals(OAuth2Provider.KAKAO.getName())) {
            return new KakaoUserInfo(
                    String.valueOf(oAuth2User.getAttributes().get("id")),
                    (Map) oAuth2User.getAttributes().get("kakao_account")
            );
        }
        log.error("지원하지 않는 인증 서버={}", registrationId);
        throw new IllegalArgumentException("지원하지 않는 서버: " + registrationId);
    }
}
