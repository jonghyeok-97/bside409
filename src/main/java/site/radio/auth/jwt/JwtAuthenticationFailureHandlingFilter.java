package site.radio.auth.jwt;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import site.radio.error.CustomErrorResponse;
import site.radio.error.ExceptionType;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFailureHandlingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException e) {
            ExceptionType unauthorizedExceptionType = ExceptionType.UNAUTHORIZED_EXCEPTION;
            response.setStatus(unauthorizedExceptionType.getHttpStatus().value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            new ObjectMapper().writeValue(response.getWriter(),
                    new CustomErrorResponse(Instant.now(), unauthorizedExceptionType.getMessageKey(), "인증에 실패했습니다."));
        }
    }
}
