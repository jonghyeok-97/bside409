package bsise.server.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>OAUTH: OAuth 로그인 유저</p>
 * <p>GUEST: guest 게스트 유저</p>
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    OAUTH("ROLE_OAUTH"),
    GUEST("ROLE_GUEST"),
    ;

    private final String role;
}
