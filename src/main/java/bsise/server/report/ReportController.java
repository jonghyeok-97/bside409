package bsise.server.report;

import bsise.server.report.daily.DailyReportDto;
import bsise.server.report.daily.DailyReportResponseDto;
import bsise.server.report.weekly.dto.WeeklyReportGetRequestDto;
import bsise.server.report.weekly.dto.WeeklyReportRequestDto;
import bsise.server.report.weekly.dto.WeeklyReportResponseDto;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/daily")
    @ResponseStatus(HttpStatus.CREATED)
    public DailyReportResponseDto createDailyReport(@Valid @RequestBody DailyReportDto.CreateRequest dailyReportDto) {
        return reportService.createDailyReport(dailyReportDto);
    }

    @GetMapping("/daily")
    @ResponseStatus(HttpStatus.OK)
    public DailyReportResponseDto getDailyReport(
            @RequestParam LocalDate date, @Valid @RequestBody DailyReportDto.GetRequest dailyReportDto) {
        return reportService.getDailyReport(dailyReportDto.getUserId(), date);
    }

    @PostMapping("/weekly")
    @ResponseStatus(HttpStatus.CREATED)
    public WeeklyReportResponseDto createWeeklyReport(
            @Valid @RequestBody WeeklyReportRequestDto weeklyReportRequestDto) {
        return reportService.createWeeklyReport(weeklyReportRequestDto);
    }

    @GetMapping("/weekly")
    @ResponseStatus(HttpStatus.OK)
    public WeeklyReportResponseDto getWeeklyReport(@RequestParam LocalDate startDate,
                                                   @RequestParam LocalDate endDate,
                                                   @Valid @RequestBody WeeklyReportGetRequestDto dto) {
        return reportService.getWeeklyReport(UUID.fromString(dto.getUserId()), startDate, endDate);
    }
}
