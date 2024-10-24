package bsise.server.limiter;

import java.util.Arrays;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateLimitPolicy {

    TTL("limit_policy:ttl:", value -> value > Constants.MIN_TTL, Constants.MIN_TTL, Constants.GT),
    LIMIT("limit_policy:limit:", value -> value < Constants.MAX_LIMIT, Constants.MAX_LIMIT, Constants.LT),
    ;

    private final String redisKey;
    private final Predicate<Integer> allowableRange;
    private final int standardValue;
    private final String condition;

    public static boolean isSupported(final String target) {
        return Arrays.stream(values())
                .anyMatch(policy -> policy.name().equalsIgnoreCase(target));
    }

    public static boolean isExpirationPolicy(final String target) {
        return TTL.name().equalsIgnoreCase(target);
    }

    public static boolean isLimitCountPolicy(final String target) {
        return LIMIT.name().equalsIgnoreCase(target);
    }

    public static boolean isNotAllowableRange(RateLimitPolicy target, final int value) {
        return !Arrays.stream(values())
                .filter(policy -> policy.equals(target))
                .findAny()
                .map(policy -> policy.allowableRange.test(value))
                .orElseThrow();
    }

    private static class Constants {
        public static final int MIN_TTL = 60 * 60 - 1;
        public static final int MAX_LIMIT = 20;
        public static final String LT = "lt";
        public static final String GT = "gt";
    }
}
