package bsise.server.report;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
}
