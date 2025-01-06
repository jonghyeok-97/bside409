package site.radio.auth;

import static site.radio.auth.jwt.JwtConstant.ACCESS_VALID_MILLIS;
import static site.radio.auth.jwt.JwtConstant.REFRESH_VALID_MILLIS;

import site.radio.auth.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${security.base-url}")
    private String baseUrl;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("========= OAuth2 success handler start =======");
        // authentication 으로부터 프로필 이미지 포함한 클레임 생성
        Claims claims = jwtService.makeNewClaims(authentication);

        // access token, refresh token 발행
        String accessToken = jwtService.issueAccessToken(claims);
        String refreshToken = jwtService.issueRefreshToken(claims);

        Cookie accessCookie = createJwtCookie("jwt-access", ACCESS_VALID_MILLIS / 1000, accessToken);
        Cookie refreshCookie = createJwtCookie("jwt-refresh", REFRESH_VALID_MILLIS / 1000, refreshToken);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

        response.sendRedirect(baseUrl + "/");
        log.info("========= OAuth2 success handler end =======");
    }

    private Cookie createJwtCookie(String cookieKey, int expireAt, String jwt) {
        Cookie cookie = new Cookie(cookieKey, jwt);
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setHttpOnly(false); // FIXME: 개발 중에만 사용
        cookie.setMaxAge(expireAt);

        return cookie;
    }
}
