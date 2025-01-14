package site.radio.report.weekly.service;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.radio.report.weekly.dto.WeeklyReportResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyFacade {

    private final WeeklyReportService weeklyReportService;

    public WeeklyReportResponseDto createWeeklyReport(UUID userId, LocalDate startDate) {
        weeklyReportService.vaildateWeeklyReportBy(userId, startDate);

        return null;
    }
}
