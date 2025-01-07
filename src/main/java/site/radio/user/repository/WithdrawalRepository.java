package site.radio.user.repository;

import site.radio.user.domain.User;
import site.radio.user.domain.WithdrawalUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalUser, Long> {

    Optional<WithdrawalUser> findByUser(User user);
}
