package site.admin.retrieve.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/users")
public class UserRetrieveController {

    private final UserRetrieveService userRetrieveService;
    private final RemainingRetrieveService remainingRetrieveService;

    @GetMapping
    public Page<UserRetrieveResult> retrieveUsers(@PageableDefault(direction = Direction.DESC) Pageable pageable) {
        return userRetrieveService.retrieveUsers(pageable);
    }

    @GetMapping("/{userId}/remaining")
    public RemainingRetrieveResult retrieveRemaining(@PathVariable("userId") String userId) {
        return remainingRetrieveService.retrieveRemaining(userId);
    }
}
