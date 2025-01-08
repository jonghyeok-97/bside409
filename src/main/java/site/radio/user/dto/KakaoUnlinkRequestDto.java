package site.radio.user.dto;

import site.radio.auth.KakaoUserInfo;
import site.radio.user.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakaoUnlinkRequestDto {

    public static final int KAKAO_ID_POSITION = 0;

    @JsonProperty(value = "target_id_type")
    private String targetIdType = "user_id";

    @JsonProperty(value = "target_id")
    private final String targetId;

    public static KakaoUnlinkRequestDto of(User user) {
        return new KakaoUnlinkRequestDto(extractKakaoIdByUsername(user.getUsername()));
    }

    public static String extractKakaoIdByUsername(String username) {
        return username.split(KakaoUserInfo.DELIMITER)[KAKAO_ID_POSITION];
    }
}
