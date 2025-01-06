package site.radio.report.daily.dto;

import site.radio.report.daily.domain.DailyReport;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyStaticsOneWeekResponseDto {

  private List<DailyReport> dailyReports;
  private DailyReportStaticsDto staticsDto;

  public static DailyStaticsOneWeekResponseDto of(List<DailyReport> dailyReports,
                                                  DailyReportStaticsDto staticsDto) {
    return DailyStaticsOneWeekResponseDto.builder()
        .dailyReports(dailyReports)
        .staticsDto(staticsDto)
        .build();
  }
}
