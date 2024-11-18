package bsise.server.report.daily;

import bsise.server.report.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, UUID> {

    @Query(value = "SELECT COUNT(d) > 0 FROM DailyReport d " +
            "JOIN Letter l ON d.id = l.dailyReport.id " +
            "WHERE d.targetDate = :targetDate AND l.user.id = :userId")
    boolean existsByUserAndTargetDate(UUID userId, LocalDate targetDate);

    @Query(value = "SELECT d FROM DailyReport d " +
            "JOIN Letter l ON d.id = l.dailyReport.id " +
            "WHERE d.targetDate = :targetDate AND l.user.id = :userId")
    Optional<DailyReport> findByUserAndTargetDate(UUID userId, LocalDate targetDate);
}
