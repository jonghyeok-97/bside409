package bsise.server.auth;

import static bsise.server.auth.jwt.JwtConstant.X_REFRESH_TOKEN;

import bsise.server.auth.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
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

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        response.setHeader(X_REFRESH_TOKEN, "Bearer " + refreshToken);
        response.sendRedirect(baseUrl + "/");
        log.info("========= OAuth2 success handler end =======");
    }
}
