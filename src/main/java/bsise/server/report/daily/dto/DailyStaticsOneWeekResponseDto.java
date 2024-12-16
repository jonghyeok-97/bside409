package bsise.server.report.daily.dto;

import bsise.server.report.daily.domain.DailyReport;
import bsise.server.report.weekly.dto.WeeklyPublishedStaticsDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyStaticsOneWeekResponseDto {

  private List<DailyReport> dailyReports;
  private WeeklyPublishedStaticsDto staticsDto;

  public static DailyStaticsOneWeekResponseDto of(List<DailyReport> dailyReports,
                                                  WeeklyPublishedStaticsDto staticsDto) {
    return DailyStaticsOneWeekResponseDto.builder()
        .dailyReports(dailyReports)
        .staticsDto(staticsDto)
        .build();
  }
}
