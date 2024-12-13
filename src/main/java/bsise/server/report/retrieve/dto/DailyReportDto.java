package bsise.server.report.retrieve.dto;

import bsise.server.report.daily.domain.CoreEmotion;
import java.time.LocalDateTime;

public interface DailyReportDto {

    String getDailyReportId();

    CoreEmotion getCoreEmotion();

    LocalDateTime getCreatedAt();
}
