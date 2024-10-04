package bsise.server.limiter;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final String USER_KEY_REMAINING = "user:%s:remaining";

    @Value("${redis.limit}")
    private long REQUEST_LIMIT;

    @Value("${redis.expire}")
    private long EXPIRE_TIME;

    private final RedisTemplate<String, String> redisTemplate;

    // check
    public boolean isRequestAllowed(String userId) {
        String key = getKey(userId);

        // 요청 횟수 증가
        Long requestCount = redisTemplate.opsForValue().increment(key, 1);

        if (requestCount == 1) {
            // 최초 요청일 경우 만료시간 설정
            redisTemplate.expire(key, EXPIRE_TIME, TimeUnit.SECONDS);
        }

        // 요청 횟수가 제한을 초과하면 false 반환
        return requestCount <= REQUEST_LIMIT;
    }

    private String getKey(String userId) {
        return String.format(USER_KEY_REMAINING, userId);
    }
}
