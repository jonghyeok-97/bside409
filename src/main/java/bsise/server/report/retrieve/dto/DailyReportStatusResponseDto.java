package bsise.server.report.retrieve.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DailyReportStatusResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private final LocalDate date;

    private final int totalCount;

    private final boolean available;

    private final boolean analyzed;

    public static DailyReportStatusResponseDto create(LocalDate date, List<DailyReportDto> letters) {
        boolean analyzed = letters.stream()
                .allMatch(letter -> letter.getDailyReportId() != null);

        return new DailyReportStatusResponseDto(date, letters.size(), !analyzed, analyzed);
    }

    public static DailyReportStatusResponseDto createFalseStatus(LocalDate date) {
        return new DailyReportStatusResponseDto(date, 0, false, false);
    }
}
