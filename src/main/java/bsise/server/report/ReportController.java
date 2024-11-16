package bsise.server.report;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/daily")
    @ResponseStatus(HttpStatus.OK)
    public DailyReportResponseDto createDailyReport(@Valid @RequestBody DailyReportRequestDto dailyReportDto) {
        return reportService.createDailyReport(dailyReportDto);
    }
}
