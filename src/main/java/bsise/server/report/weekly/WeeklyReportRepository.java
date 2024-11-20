package bsise.server.report.weekly;

import bsise.server.report.WeeklyReport;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, UUID> {
}
