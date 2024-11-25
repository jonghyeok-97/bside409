package bsise.server.report.weekly.controller;

import bsise.server.report.weekly.dto.WeeklyReportGetRequestDto;
import bsise.server.report.weekly.dto.WeeklyReportRequestDto;
import bsise.server.report.weekly.dto.WeeklyReportResponseDto;
import bsise.server.report.weekly.service.WeeklyReportService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
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
public class WeeklyReportController {

    private final WeeklyReportService weeklyReportService;

    @PostMapping("/api/v1/reports/weekly")
    @ResponseStatus(HttpStatus.CREATED)
    public WeeklyReportResponseDto createWeeklyReport(
            @Valid @RequestBody WeeklyReportRequestDto weeklyReportRequestDto) {
        return weeklyReportService.createWeeklyReport(weeklyReportRequestDto);
    }

    @GetMapping("/api/v1/reports/weekly")
    @ResponseStatus(HttpStatus.OK)
    public WeeklyReportResponseDto getWeeklyReport(@RequestParam LocalDate startDate,
                                                   @RequestParam LocalDate endDate,
                                                   @Valid @RequestBody WeeklyReportGetRequestDto dto) {
        return weeklyReportService.getWeeklyReport(UUID.fromString(dto.getUserId()), startDate, endDate);
    }
}
