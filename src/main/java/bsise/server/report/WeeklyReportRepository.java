package bsise.server.report;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, UUID> {

    Optional<WeeklyReport> findWeeklyReportByStartDateIsAndEndDateIs(LocalDate startDate, LocalDate endDate);
}
