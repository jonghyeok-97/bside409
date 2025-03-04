package site.radio.auth;

import site.radio.error.DormantUserLoginException;
import site.radio.error.UserNotFoundException;
import site.radio.user.domain.User;
import site.radio.user.domain.WithdrawalUser;
import site.radio.user.repository.UserRepository;
import site.radio.user.repository.WithdrawalRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    private static final int ALLOWED_RECOVERY_DAY = 14;
    private final UserRepository userRepository;
    private final WithdrawalRepository withdrawalRepository;
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
            WithdrawalUser withdrawalUser = withdrawalRepository.findByUser(user)
                    .orElseThrow(() -> new UserNotFoundException("no user found with userId: " + user.getId()));

            // 복구 허용 범위가 아니면 이미 삭제되야 하는 계정(삭제 진행 중일 수도 있음)
            if (isOutOfRecoveryRange(user.getDormantAt())) {
                throw new DormantUserLoginException("dormant exception: 탈퇴 진행 중인 계정입니다. 재시도를 해보세요.");
            }

            // 휴면 계정으로부터 계정을 복구
            withdrawalRepository.delete(withdrawalUser);
            user.recoverFromDormantAccount();
            log.info("--- 휴면 유저 복구 처리 완료: {} ---", user.getId());
        }

        // 기존 유저 => UserDetails 반환
        request.setAttribute("userId", user.getId());
        log.info("--- 기존 OAuth2 유저 ID: {} ---", user.getId());
        return new UpUserDetails(user, oAuth2User.getAttributes());
    }

    /**
     * {@code 휴면 시작일 + 복구 허용일 < 현재 시간} 이면 이미 복구 허용일이 지났으므로 복구 범위를 벗어난 것으로 판별합니다.
     *
     * @param dormantAt 휴면 시작일
     * @return 복구 범위 초과 여부
     */
    private boolean isOutOfRecoveryRange(LocalDateTime dormantAt) {
        return LocalDateTime.now().isAfter(dormantAt.plusDays(ALLOWED_RECOVERY_DAY));
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
