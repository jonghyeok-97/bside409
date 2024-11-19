package bsise.server.report;

import bsise.server.report.weekly.dto.WeeklyReportRequestDto;
import bsise.server.report.weekly.dto.WeeklyReportResponseDto;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping(value = "/daily", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DailyReportResponseDto createDailyReport(@Valid @RequestBody DailyReportRequestDto dailyReportDto) {
        return reportService.createDailyReport(dailyReportDto);
    }

    @GetMapping(value = "/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public DailyReportResponseDto getDailyReport(
            @RequestParam LocalDate date, @Validated(DailyReportGetRequest.class) @RequestBody DailyReportRequestDto dailyReportDto) {
        return reportService.getDailyReport(dailyReportDto.getUserId(), date);
    }

    @PostMapping("/weekly")
    @ResponseStatus(HttpStatus.CREATED)
    public WeeklyReportResponseDto createWeeklyReport(@Valid @RequestBody WeeklyReportRequestDto weeklyReportRequestDto) {
        return reportService.createWeeklyReport(weeklyReportRequestDto);
    }

    @GetMapping("/weekly")
    @ResponseStatus(HttpStatus.OK)
    public WeeklyReportResponseDto getWeeklyReport(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return reportService.getWeeklyReport(startDate, endDate);
    }
}
