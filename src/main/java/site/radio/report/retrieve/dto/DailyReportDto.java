package site.radio.report.retrieve.dto;

import site.radio.report.daily.domain.CoreEmotion;
import java.time.LocalDateTime;

public interface DailyReportDto {

    String getDailyReportId();

    CoreEmotion getCoreEmotion();

    LocalDateTime getCreatedAt();
}
