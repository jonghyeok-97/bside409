package site.radio.auth;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {

    KAKAO("kakao"),
    UNKNOWN("guest"),
    ;

    private final String name;

    public static OAuth2Provider fromString(String provider) {
        return Arrays.stream(values())
                .filter(p -> p.name.equals(provider.toLowerCase()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown provider " + provider));
    }
}
