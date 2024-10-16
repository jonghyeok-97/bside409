package bsise.admin.retrieve.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserRetrieveService {

    private final UserRepository userRepository;

    public Page<UserRetrieveResult> retrieveUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        List<UserRetrieveResult> content = users.stream()
                .map(UserRetrieveResult::of)
                .toList();

        return new PageImpl<>(content, pageable, users.getTotalElements());
    }
}