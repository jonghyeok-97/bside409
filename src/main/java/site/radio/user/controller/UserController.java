package site.radio.user.controller;

import site.radio.limiter.RateLimitService;
import site.radio.limiter.UserUsageResponseDto;
import site.radio.user.dto.UserChangeRequestDto;
import site.radio.user.dto.UserDeleteRequestDto;
import site.radio.user.dto.UserDeleteResponseDto;
import site.radio.user.dto.UserResponseDto;
import site.radio.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @Operation(summary = "유저 사용량 조회 API", description = "유저의 편지 쓰기 횟수 및 초기화까지 남은 시간을 조회합니다.")
    @GetMapping("/{userId}/usage")
    public UserUsageResponseDto getUserUsage(@PathVariable("userId") String userId) {
        return limitService.getUsageByUserId(userId);
    }

    @Operation(summary = "회원 탈퇴 API", description = "요청한 회원의 탈퇴를 처리합니다.")
    @DeleteMapping("/{userId}")
    public UserDeleteResponseDto deleteUser(@PathVariable("userId") String userId,
                                            @RequestBody UserDeleteRequestDto deleteDto) {
        return userService.deleteUser(UUID.fromString(userId), deleteDto);
    }
}
