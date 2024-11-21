package bsise.server.report;

import bsise.server.report.daily.DailyReportDto;
import bsise.server.report.daily.DailyReportResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
}
