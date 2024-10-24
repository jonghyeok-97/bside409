package bsise.server.user.dto;

import bsise.server.user.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakaoUnlinkRequestDto {

    @JsonProperty(value = "target_id_type")
    private String targetIdType = "user_id";

    @JsonProperty(value = "target_id")
    private final Long targetId;

    public static KakaoUnlinkRequestDto of(User user) {
        return new KakaoUnlinkRequestDto(Long.parseLong(user.getUsername()));
    }
}
