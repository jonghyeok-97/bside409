package bsise.server.letter;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterRepository extends JpaRepository<Letter, UUID> {

    Page<Letter> findLettersByUserId(UUID userId, Pageable pageable);
    List<Letter> findTop10ByPublishedIsTrueOrderByCreatedAtDesc();
}
