package bsise.server.auth.jwt;

import bsise.server.error.CustomErrorResponse;
import bsise.server.error.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ExceptionType unauthorizedExceptionType = ExceptionType.UNAUTHORIZED_EXCEPTION;
        response.setStatus(unauthorizedExceptionType.getHttpStatus().value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        new ObjectMapper().writeValue(response.getWriter(),
                new CustomErrorResponse(Instant.now(), unauthorizedExceptionType.getMessageKey(), "인증에 실패했습니다."));
    }
}
