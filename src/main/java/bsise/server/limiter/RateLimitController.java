package bsise.server.limiter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/policies")
@RequiredArgsConstructor
public class RateLimitController {

    private final RateLimitService rateLimitService;

    @GetMapping
    public RateLimitPolicyResponseDto getRateLimitPolicy() {
        return rateLimitService.retrieveCurrentPolicy();
    }
}
