package bsise.server.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Schema(description = "유저 정보 변경 요청 DTO")
@Data
public class UserChangeRequestDto {

    @Schema(description = "변경을 희망하는 닉네임")
    @Length(min = 1, max = 12)
    @NotNull
    private String nickname;

    @Schema(description = "선호하는 답변 타입", allowableValues = {"T", "F"})
    @NotNull
    private Preference preference;

    @Schema(description = "카카오 프로필 이미지 동기화 여부", allowableValues = {"true", "false"})
    @NotNull
    private boolean profileImageDisable;
}
