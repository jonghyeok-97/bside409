package bsise.server.user.repository;

import bsise.server.user.domain.WithdrawalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalUser, Long> {
}
