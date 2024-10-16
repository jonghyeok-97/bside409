package bsise.admin.modifier.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RateLimitChangeResult {

    private int changeResult;
    private LocalDateTime changeDateTime;

    public static RateLimitChangeResult of(int changeValue) {
        return new RateLimitChangeResult(changeValue, LocalDateTime.now());
    }
}
