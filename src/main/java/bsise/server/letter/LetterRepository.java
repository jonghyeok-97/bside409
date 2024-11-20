package bsise.server.letter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterRepository extends JpaRepository<Letter, UUID> {

    Page<Letter> findLettersByUserId(UUID userId, Pageable pageable);
    List<Letter> findTop10ByPublishedIsTrueOrderByCreatedAtDesc();

    @Query(value = "SELECT * FROM letter " +
                   "WHERE user_id = :userId AND created_at BETWEEN :startTime AND :endTime " +
                   "ORDER BY created_at DESC LIMIT 3", nativeQuery = true)
    List<Letter> find3RecentLetters(
            @Param("userId") UUID userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query(
            value = """
                SELECT l
                FROM Letter l
                WHERE l.dailyReport.id IS NULL 
                        AND l.id = :userId
                        AND l.createdAt >= :start
                        AND l.createdAt < :end
                ORDER BY l.createdAt DESC
                LIMIT 3     
            """)
    List<Letter> findThreeLettersWithoutDailyReport(UUID userId, LocalDateTime start, LocalDateTime end);
}
