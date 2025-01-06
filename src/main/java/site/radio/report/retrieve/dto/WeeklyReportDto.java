package site.radio.report.retrieve.dto;

import java.time.LocalDateTime;

public interface WeeklyReportDto {

    String getWeeklyReportId();
    LocalDateTime getLetterCreatedAt();
}
