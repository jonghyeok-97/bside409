package site.radio.auth;

import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoUserInfo implements OAuth2UserInfo {

    public static final String DELIMITER = "@";
    private final String id;
    private final Map<String, Object> attributes;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getProvider() {
        return OAuth2Provider.KAKAO.getName();
    }

    @Override
    public String getUserName() {
        return id + DELIMITER + OAuth2Provider.KAKAO.getName();
    }

    @Override
    public String getEmail() {
        return String.valueOf(attributes.get("email"));
    }

    @Override
    public String getProfileImage() {
        return String.valueOf(((Map<?, ?>) attributes.get("profile")).get("profile_image_url"));
    }
}
