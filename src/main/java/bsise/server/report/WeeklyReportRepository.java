package bsise.server.report;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, UUID> {
}
