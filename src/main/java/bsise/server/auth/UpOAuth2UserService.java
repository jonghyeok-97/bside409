package bsise.server.auth;

import bsise.server.error.DormantUserLoginException;
import bsise.server.error.UserNotFoundException;
import bsise.server.user.domain.User;
import bsise.server.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final HttpServletRequest request;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // registrationId 확인
        log.info("client registration={}", userRequest.getClientRegistration());
        log.info("access token={}", userRequest.getAccessToken().getTokenValue());
        log.info("attributes={}", super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = extractOAuth2UserInfo(userRequest, oAuth2User);

        Optional<User> optionalUser = userRepository.findByUsername(oAuth2UserInfo.getUserName());

        // 신규 OAuth2 유저 => 저장
        if (optionalUser.isEmpty()) {
            User newUser = userRepository.save(User.makeFromOAuth2UserInfo(oAuth2UserInfo));
            request.setAttribute("userId", newUser.getId());
            log.info("--- 신규 OAuth2 유저: {} ---", newUser.getId());
            return new UpUserDetails(newUser, oAuth2User.getAttributes());
        }

        // 휴면 유저 또는 탈퇴 중인 유저 => 복구 페이지로 redirection
        User user = optionalUser.get();
        if (user.isDormant()) {
            log.info("--- 휴면 유저 로그인 시도: {} ---", user.getId());
            request.setAttribute("userId", user.getId().toString());
            throw new DormantUserLoginException("dormant exception: 탈퇴 진행 중인 계정입니다.");
        }

        // 기존 유저 => UserDetails 반환
        request.setAttribute("userId", user.getId());
        log.info("--- 기존 OAuth2 유저 ID: {} ---", user.getId());
        return new UpUserDetails(user, oAuth2User.getAttributes());
    }

    public boolean isOAuth2User(String userId) {
        return userRepository.existsUserById(UUID.fromString(userId));
    }

    public UserDetails loadUserByUsername(String userId) throws OAuth2AuthenticationException {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("no user found with userId: " + userId));

        return new UpUserDetails(user);
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
