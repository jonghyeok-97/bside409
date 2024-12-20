package bsise.server.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static bsise.server.auth.jwt.JwtConstant.ACCESS_TOKEN_EXPIRED;
import static bsise.server.auth.jwt.JwtConstant.X_REFRESH_TOKEN;

@Slf4j
@Profile({"dev"})
@RequiredArgsConstructor
public class JwtRefresherFilter extends OncePerRequestFilter {

    private static final String[] NOT_FILTERED_URLS = {
            "/login*", "/oauth2*", "/error", "/swagger-*", "/v3/api-docs*", "/api-docs*", "/api/v1/users*"
    };
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("=== jwt refresher filter start ===");

        Boolean isAccessTokenExpired = (Boolean) request.getAttribute(ACCESS_TOKEN_EXPIRED);
        if (isAccessTokenExpired == null) {
            isAccessTokenExpired = false;
        }
        log.info("=== isAccessTokenExpired : {} ===", isAccessTokenExpired);

        if (isAccessTokenExpired) {
            String refreshToken = jwtService.resolveRefreshToken(request);

            log.info("=== refreshToken: {} ===", refreshToken);

            if (refreshToken == null) {
                throw new BadCredentialsException("Invalid refresh token.");
            }

            try {
                if (jwtService.isValidRefreshToken(refreshToken)) {
                    // security context 저장
                    Authentication authentication = jwtService.getAuthenticationFromRefreshToken(refreshToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    String reIssuedAccessToken = jwtService.reIssueAccessToken(refreshToken);
                    String reIssuedRefreshToken = jwtService.reIssueRefreshToken(refreshToken);

                    // 재발급된 accessToken, refreshToken 반환
                    response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + reIssuedAccessToken);
                    response.setHeader(X_REFRESH_TOKEN, "Bearer " + reIssuedRefreshToken);
                    log.info("=== JWT TOKEN REISSUED ===");
                }
            } catch (ExpiredJwtException e) {
                log.info("=== refresh token expired. ===");
                throw new BadCredentialsException("Refresh Token Expired.");
            }
        }

        filterChain.doFilter(request, response);
        log.info("===jwt refresher filter end===");
    }

    /**
     * servlet path 가 허용된 로그인 URL 이 아닐 때는 해당 필터를 작동시키지 않도록 합니다.
     *
     * @param request HttpServletRequest
     * @return true: 해당 필터 적용하지 않음, false: 해당 필터를 적용함
     * @throws ServletException 서블릿 예외
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return PatternMatchUtils.simpleMatch(NOT_FILTERED_URLS, request.getServletPath());
    }
}
