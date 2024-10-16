package bsise.admin.retrieve.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RemainingRetrieveService {

    public static final int LIMIT = 10; // TODO: spring cloud config 적용
    private static final String USER_KEY_REMAINING = "user:%s:remaining";

    private final RedisTemplate<String, String> redisTemplate;

    public RemainingRetrieveResult retrieveRemaining(String userId) {
        String key = getKey(userId);

        String remaining = redisTemplate.opsForValue().get(key);

        return RemainingRetrieveResult.of(userId, convertToInt(remaining));
    }

    private String getKey(String userId) {
        return String.format(USER_KEY_REMAINING, userId);
    }

    private int convertToInt(String remaining) {
        return StringUtils.hasText(remaining) ? LIMIT - Integer.parseInt(remaining) : LIMIT;
    }
}
