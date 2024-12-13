package bsise.server.report.retrieve.dto;

import bsise.server.report.daily.domain.CoreEmotion;
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

    private final CoreEmotion coreEmotion;

    private final boolean available;

    public static DailyReportStatusResponseDto create(LocalDate date, List<DailyReportDto> letters) {
        boolean analyzed = letters.stream()
                .anyMatch(letter -> letter.getDailyReportId() != null);

        CoreEmotion coreEmotion = letters.stream()
                .filter(letter -> letter.getDailyReportId() != null)
                .findAny()
                .map(DailyReportDto::getCoreEmotion)
                .orElse(null);

        return new DailyReportStatusResponseDto(date, coreEmotion, !analyzed);
    }

    public static DailyReportStatusResponseDto createFalseStatus(LocalDate date) {
        return new DailyReportStatusResponseDto(date, null, false);
    }
}
