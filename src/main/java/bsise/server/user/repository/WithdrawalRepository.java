package bsise.server.user.repository;

import bsise.server.user.domain.User;
import bsise.server.user.domain.WithdrawalUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalUser, Long> {

    Optional<WithdrawalUser> findByUser(User user);
}
