package bsise.admin.modifier;

import static bsise.admin.modifier.RateLimitPolicy.*;

import bsise.admin.modifier.dto.RateLimitChangeResult;
import bsise.admin.modifier.dto.RateLimitSupportPolicy;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitManager {

    private static final String MESSAGE_FORMAT = "허용된 범위의 값이 아닙니다. => %d";

    private final RedisTemplate<String, String> redisTemplate;

    public List<RateLimitSupportPolicy> getSupportedPolices() {
        String currentTTL = redisTemplate.opsForValue().get(TTL.getRedisKey());
        String currentLimit = redisTemplate.opsForValue().get(LIMIT.getRedisKey());

        return Arrays.stream(values())
                .map(policy -> {
                    if (policy == TTL) {
                        return RateLimitSupportPolicy.of(policy, currentTTL);
                    }
                    return RateLimitSupportPolicy.of(policy, currentLimit);
                })
                .toList();
    }

    public RateLimitChangeResult change(String target, int changeValue) {
        validateMinus(changeValue);
        validatePolicy(target);

        if (isLimitCountPolicy(target)) {
            changeLimit(changeValue);
            return RateLimitChangeResult.of(changeValue);
        }

        changeExpire(changeValue);
        return RateLimitChangeResult.of(changeValue);
    }

    private void validatePolicy(String target) {
        if (!isSupported(target)) {
            log.error("변경 정책 대상 오류 : {}", target);
            throw new IllegalArgumentException("변경하려는 대상이 잘못 되었습니다. => " + target);
        }
    }

    private void changeExpire(int expireAt) {
        if (isNotAllowableRange(TTL, expireAt)) {
            throw new IllegalArgumentException(String.format(MESSAGE_FORMAT, expireAt));
        }
        String before = redisTemplate.opsForValue().get(TTL.getRedisKey());
        redisTemplate.opsForValue().set(TTL.getRedisKey(), String.valueOf(expireAt));
        log.info("=== {} 정책 변경 성공 - 변경 전: {} 변경 후: {} ===", TTL, before, expireAt);
    }

    private void changeLimit(int limit) {
        if (isNotAllowableRange(LIMIT, limit)) {
            throw new IllegalArgumentException(String.format(MESSAGE_FORMAT, limit));
        }
        String before = redisTemplate.opsForValue().get(LIMIT.getRedisKey());
        redisTemplate.opsForValue().set(LIMIT.getRedisKey(), String.valueOf(limit));
        log.info("=== {} 정책 변경 성공 - 변경 전: {} 변경 후: {} ===", LIMIT, before, limit);
    }

    private void validateMinus(int value) {
        if (value < 0) {
            log.error("변경 값 오류 : {}", value);
            throw new IllegalArgumentException("변경하려는 값이 음수일 수는 없습니다. => " + value);
        }
    }
}
