package bsise.server.report.retrieve;

import bsise.server.report.retrieve.dto.DailyReportStatusResponseDto;
import bsise.server.report.retrieve.dto.ReportStatusRequestDto;
import bsise.server.report.retrieve.dto.WeeklyReportStatusResponseDto;
import bsise.server.report.retrieve.service.ReportStatusRetrieveService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportStatusRetrieveController {

    private final ReportStatusRetrieveService reportStatusRetrieveService;

    @GetMapping(value = "/api/v1/reports/daily/status")
    @ResponseStatus(HttpStatus.OK)
    public List<DailyReportStatusResponseDto> retrieveDailyReportStatus(
            @Valid @RequestBody ReportStatusRequestDto requestDto
    ) {
        LocalDate now = LocalDate.now();
        return reportStatusRetrieveService.findDailyReportStatus(UUID.fromString(requestDto.getUserId()), now, now);
    }

    @GetMapping(value = "/api/v1/reports/weekly/status")
    @ResponseStatus(HttpStatus.OK)
    public List<WeeklyReportStatusResponseDto> retrieveWeeklyReportStatus(
            @Valid @RequestBody ReportStatusRequestDto requestDto
    ) {
        LocalDate now = LocalDate.now();
        return reportStatusRetrieveService.findWeeklyReportStatus(UUID.fromString(requestDto.getUserId()), now, now);
    }
}
