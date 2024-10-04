package bsise.server.user;

import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto changeUserInfo(UUID userId, UserChangeRequestDto changeDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.changeNickname(changeDto.getNickname());
        user.changePreference(changeDto.getPreference());
        user.changeSynced(changeDto.isProfileImageDisable());

        return UserResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return UserResponseDto.of(user);
    }
}
