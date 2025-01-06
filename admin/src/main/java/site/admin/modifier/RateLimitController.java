package site.admin.modifier;

import site.admin.modifier.dto.RateLimitChangeRequest;
import site.admin.modifier.dto.RateLimitChangeResult;
import site.admin.modifier.dto.RateLimitSupportPolicy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/policy")
public class RateLimitController {

    private final RateLimitManager rateLimitManager;

    @GetMapping
    public List<RateLimitSupportPolicy> getSupportedPolicies() {
        return rateLimitManager.getSupportedPolices();
    }

    @PutMapping
    public RateLimitChangeResult changePolicy(@RequestBody RateLimitChangeRequest request) {
        return rateLimitManager.change(request.getTarget(), request.getChangeValue());
    }
}
