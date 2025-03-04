package site.radio.report.retrieve.controller;

import site.radio.report.retrieve.dto.DailyReportStatusResponseDto;
import site.radio.report.retrieve.dto.WeeklyReportStatusResponseDto;
import site.radio.report.retrieve.service.ReportStatusRetrieveService;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportStatusRetrieveController {

    private final ReportStatusRetrieveService reportStatusRetrieveService;

    @GetMapping(value = "/api/v1/reports/daily/status/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<DailyReportStatusResponseDto> retrieveDailyReportStatus(
            @PathVariable("userId") UUID userId, @RequestParam("yearMonth") YearMonth yearMonth
    ) {
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        return reportStatusRetrieveService.findDailyReportStatus(userId, endOfMonth, endOfMonth);
    }

    @GetMapping(value = "/api/v1/reports/weekly/status/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<WeeklyReportStatusResponseDto> retrieveWeeklyReportStatus(
            @PathVariable("userId") UUID userId, @RequestParam("yearMonth") YearMonth yearMonth
    ) {
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        return reportStatusRetrieveService.findWeeklyReportStatus(userId, endOfMonth, endOfMonth);
    }
}
