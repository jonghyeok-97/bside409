package bsise.server.user;

import bsise.server.limiter.RateLimitService;
import bsise.server.limiter.UserUsageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "user", description = "유저 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final RateLimitService limitService;

    @Operation(summary = "유저 정보 변경 API", description = "유저의 정보를 변경합니다.")
    @PatchMapping("/{userId}")
    public UserResponseDto changeUserInfo(@PathVariable("userId") String userId,
                                          @RequestBody UserChangeRequestDto changeDto) {
        return userService.changeUserInfo(UUID.fromString(userId), changeDto);
    }

    @Operation(summary = "유저 정보 조회 API", description = "유저의 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public UserResponseDto getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUser(UUID.fromString(userId));
    }

    @Operation(summary = "유저 정보 조회 테스트 API", description = "유저의 정보 조회 테스트")
    @GetMapping("/{userId}/test")
    public UserResponseDto testGetUserInfo(@PathVariable("userId") String userId) {
        log.info("Get user info by user id {}", userId);
        return new UserResponseDto(userId, "임시 닉네임", "test@test.com", Preference.F, false, true);
    }

    @Operation(summary = "유저 사용량 조회 API", description = "유저의 편지 쓰기 횟수 및 초기화까지 남은 시간을 조회합니다.")
    @GetMapping("/{userId}/usage")
    public UserUsageResponseDto getUserUsage(@PathVariable("userId") String userId) {
        return limitService.getUsageByUserId(userId);
    }
}
