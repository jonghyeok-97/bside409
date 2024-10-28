package bsise.server.user.service;

import bsise.server.error.UserNotFoundException;
import bsise.server.user.dto.KakaoUnlinkRequestDto;
import bsise.server.user.dto.UserChangeRequestDto;
import bsise.server.user.dto.UserDeleteRequestDto;
import bsise.server.user.dto.UserDeleteResponseDto;
import bsise.server.user.dto.UserResponseDto;
import bsise.server.user.domain.WithdrawalUser;
import bsise.server.user.domain.User;
import bsise.server.user.repository.UserRepository;
import bsise.server.user.repository.WithdrawalRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final KakaoOAuth2Client client;

    @Value("${security.kakao-admin-key}")
    private String kakaoAdminKey;

    public UserResponseDto changeUserInfo(UUID userId, UserChangeRequestDto changeDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.changeNickname(changeDto.getNickname());
        user.changePreference(changeDto.getPreference());
        user.changeSynced(changeDto.isProfileImageEnabled());
        user.changeEmailAdsConsent(changeDto.isEmailAdsConsented());
        user.changeAgreeToTerms(changeDto.isAgreeToTerms());
        user.changeAgreeToPrivacyPolicy(changeDto.isAgreeToPrivacyPolicy());

        return UserResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return UserResponseDto.of(user);
    }

    public UserDeleteResponseDto deleteUser(UUID userId, UserDeleteRequestDto deleteRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        /* 휴면 계정으로 전환 */
        user.changeToDormantAccount();
        WithdrawalUser withdrawalUser = WithdrawalUser.toWithdrawalUser(user, deleteRequestDto);
        withdrawalRepository.save(withdrawalUser);
        KakaoUnlinkRequestDto dto = KakaoUnlinkRequestDto.of(user);

        /* kakao oauth2 서버에 unlink 요청 전송 */
        client.requestUnlinkByUser(kakaoAdminKey, dto.getTargetIdType(), dto.getTargetId());

        return UserDeleteResponseDto.of(user);
    }
}
