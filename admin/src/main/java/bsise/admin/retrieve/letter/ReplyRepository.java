package bsise.admin.retrieve.letter;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, UUID> {

    @Query("SELECT r FROM Reply r join fetch r.letter l join fetch l.user u ORDER BY r.createdAt DESC")
    Page<Reply> findMessages(Pageable pageable);
}
