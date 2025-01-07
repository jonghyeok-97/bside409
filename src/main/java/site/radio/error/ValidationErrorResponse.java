package site.radio.error;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ValidationErrorResponse {
    private final Instant timestamp;
    private final String key;
    private final List<ValidationProblemDetails> message;

    public static ValidationErrorResponse of(ExceptionType exceptionType,
                                             List<ValidationProblemDetails> message) {
        return ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .key(exceptionType.getMessageKey())
                .message(message)
                .build();
    }
}
