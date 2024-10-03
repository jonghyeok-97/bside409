package bsise.server.auth.jwt;

import static bsise.server.auth.jwt.JwtConstant.X_REFRESH_TOKEN;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGeneratorFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("=== jwt generator filter start ===");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보 없으면 조기 종료
        if (authentication == null) {
            return;
        }

        // authentication 으로부터 프로필 이미지 포함한 클레임 생성
        Claims claims = jwtService.makeNewClaims(authentication);

        // access token, refresh token 발행
        String accessToken = jwtService.issueAccessToken(claims);
        String refreshToken = jwtService.issueRefreshToken(claims);

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        response.setHeader(X_REFRESH_TOKEN, "Bearer " + refreshToken);

        filterChain.doFilter(request, response);
        log.debug("=== jwt generator filter end ===");
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
        return !request.getServletPath().startsWith("/login");
//        return Stream.of(
//                        "/login"
//                )
//                .noneMatch(url -> url.startsWith(request.getRequestURI())); // none match 라면 true 반환(해당 필터 적용 x)
    }
}
