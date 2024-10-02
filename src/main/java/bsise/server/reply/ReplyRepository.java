package bsise.server.reply;

import bsise.server.letter.Letter;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, UUID> {

    Optional<Reply> findByLetter(Letter letter);
}
