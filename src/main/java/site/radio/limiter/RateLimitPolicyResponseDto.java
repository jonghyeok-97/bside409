package site.radio.limiter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RateLimitPolicyResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Long limit;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Long ttl;

    public static RateLimitPolicyResponseDto of(Long limit, Long ttl) {
        return new RateLimitPolicyResponseDto(limit, ttl);
    }
}
