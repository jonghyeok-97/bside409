package bsise.admin.modifier.dto;

import bsise.admin.modifier.RateLimitPolicy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RateLimitSupportPolicy {

    private final RateLimitPolicy policy;
    private final int standardValue;
    private final String condition;
    private final String currentValue;

    public static RateLimitSupportPolicy of(RateLimitPolicy policy, String currentValue) {
        return new RateLimitSupportPolicy(policy, policy.getStandardValue(), policy.getCondition(), currentValue);
    }
}
