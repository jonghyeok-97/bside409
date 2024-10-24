package bsise.server.user.dto;

import bsise.server.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "회원 탈퇴 응답 DTO")
@Getter
@AllArgsConstructor
public class UserDeleteResponseDto {

    @Schema(description = "탈퇴한 유저 아이디", example = "123e4567-e89b-12d3-a456-426655440000")
    private UUID userId;

    public static UserDeleteResponseDto of(User user) {
        return new UserDeleteResponseDto(user.getId());
    }
}
