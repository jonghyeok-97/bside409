package bsise.server.error;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@RequiredArgsConstructor
public class CustomErrorResponse {

    private final Instant timestamp;
    private final String key;
    private final String message;

    public static CustomErrorResponse of(ExceptionType exceptionType, String message) {
        return CustomErrorResponse.builder()
                .timestamp(Instant.now())
                .key(exceptionType.getMessageKey())
                .message(message)
                .build();
    }
}
