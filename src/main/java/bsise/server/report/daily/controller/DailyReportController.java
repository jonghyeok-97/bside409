package bsise.server.report.daily.controller;

import bsise.server.report.daily.dto.DailyReportDto;
import bsise.server.report.daily.dto.DailyReportResponseDto;
import bsise.server.report.daily.service.DailyReportService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService dailyReportService;

    @PostMapping("/api/v1/reports/daily")
    @ResponseStatus(HttpStatus.CREATED)
    public DailyReportResponseDto createDailyReport(@Valid @RequestBody DailyReportDto.CreateRequest dailyReportDto) {
        return dailyReportService.createDailyReport(dailyReportDto);
    }

    @GetMapping("/api/v1/reports/daily")
    @ResponseStatus(HttpStatus.OK)
    public DailyReportResponseDto getDailyReport(
            @RequestParam LocalDate date, @Valid @RequestBody DailyReportDto.GetRequest dailyReportDto) {
        return dailyReportService.getDailyReport(dailyReportDto.getUserId(), date);
    }
}
