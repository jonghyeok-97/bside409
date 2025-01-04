package bsise.server.error;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {
    // 400
    ILLEGAL_ARGUMENT_EXCEPTION(BAD_REQUEST, "error.illegal.argument", IllegalArgumentException.class),
    ARGS_INVALID_EXCEPTION(BAD_REQUEST, "error.args.invalid", MethodArgumentNotValidException.class),

    // 401
    UNAUTHORIZED_EXCEPTION(UNAUTHORIZED, "error.unauthorized", BadCredentialsException.class),

    // 404
    ENTITY_NOT_FOUND_EXCEPTION(NOT_FOUND, "error.entity.not.found", EntityNotFoundException.class),

    // 409
    DORMANT_USER_LOGIN_EXCEPTION(CONFLICT, "error.dormantuser.login", DormantUserLoginException.class),
    NAMED_LOCK_EXCEPTION(CONFLICT, "error.namedLock", NamedLockAcquisitionException.class),
    ENTITY_ALREADY_EXISTS_EXCEPTION(CONFLICT, "error.entity.already.exists", EntityAlreadyExistsException.class),

    // 429
    RATE_LIMIT_EXCEPTION(TOO_MANY_REQUESTS, "error.rate.limit", RateLimitException.class),

    // 500
    ILLEGAL_STATE_EXCEPTION(INTERNAL_SERVER_ERROR, "error.illegal.state", IllegalArgumentException.class),
    CALL_NOT_PERMITTED_EXCEPTION(INTERNAL_SERVER_ERROR, "error.call.notPermitted", CallNotPermittedException.class),
    NO_FALLBACK_AVAILABLE_EXCEPTION(INTERNAL_SERVER_ERROR, "error.noFallbackAvailable", NoFallbackAvailableException.class),
    UNHANDLED_EXCEPTION(INTERNAL_SERVER_ERROR, "error.unhandled", Exception.class);   // default

    private final HttpStatus httpStatus;
    private final String messageKey;
    private final Class<? extends Exception> exception;

    /**
     * ExceptionType에서 해당 예외 클래스와 매칭되는 타입 검색
     */
    public static ExceptionType from(Exception exception) {
        return Arrays.stream(ExceptionType.values())
                .filter(type -> type.getException() != null && type.getException().isAssignableFrom(exception.getClass()))
                .findFirst()
                .orElse(ExceptionType.UNHANDLED_EXCEPTION);
    }
}