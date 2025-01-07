package bsise.server.error;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@RequiredArgsConstructor
public class CustomErrorResponse {

    private final Instant timestamp;
    private final String key;
    private final String message;

    @JsonGetter("timestamp")
    public String getFormattedTimestamp() {
        return timestamp.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ISO_INSTANT);
    }

    public static CustomErrorResponse of(ExceptionType exceptionType, String message) {
        return CustomErrorResponse.builder()
                .timestamp(Instant.now())
                .key(exceptionType.getMessageKey())
                .message(message)
                .build();
    }
}
