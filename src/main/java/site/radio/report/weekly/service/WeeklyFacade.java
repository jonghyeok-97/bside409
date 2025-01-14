package site.radio.report.weekly.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.radio.letter.Letter;
import site.radio.report.daily.service.DailyReportService;
import site.radio.report.weekly.dto.WeeklyReportResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyFacade {

    private final DailyReportService dailyReportService;
    private final WeeklyReportService weeklyReportService;

    public WeeklyReportResponseDto createWeeklyReport(UUID userId, LocalDate startDate) {
        // 주간 리포트 생성 여부 확인
        weeklyReportService.vaildateWeeklyReportBy(userId, startDate);

        // 클로바에게 요청을 보낼 일일 리포트 찾기
        Map<LocalDate, List<Letter>> dailyReportsForClovaRequest = dailyReportService.findDailyReportsForClovaRequest(
                userId, startDate, startDate.plusDays(6));

        return null;
    }
}
