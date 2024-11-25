package bsise.server.report.weekly.dto;

import bsise.server.report.weekly.service.WeeklyDataManager;
import bsise.server.report.weekly.domain.WeeklyReport;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@Tag(name = "")
@Schema(description = "유저의 주간 분석 요청이 성공하면 받는 응답 DTO")
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyReportResponseDto {

    @Schema(description = "1년 기반의 N주차")
    private int weekOfYear;

    @Schema(description = "N월 N주차")
    private String week_name;

    @Schema(description = "N월 N주차의 시작 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @Schema(description = "N월 N주차의 마지막 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;

    @Schema(description = "주간 분석에 사용된 편지 개수와 일기 개수")
    private WeeklyFrequencyDto frequency;

    // TODO: 요일별 추이

    @Schema(description = "위로 한 마디")
    private String cheerUp;

    public static WeeklyReportResponseDto from(WeeklyReport weeklyReport, WeeklyDataManager manager) {
        return WeeklyReportResponseDto.builder()
                .weekOfYear(weeklyReport.getWeekOfYear())
                .week_name(manager.getMonthValue() + "월 " + manager.getWeekOfMonth() + "주차")
                .startDate(weeklyReport.getStartDate())
                .endDate(weeklyReport.getEndDate())
                .frequency(WeeklyFrequencyDto.of(weeklyReport.getPublishedCount(), weeklyReport.getUnpublishedCount()))
                // TODO: 요일별 추이
                .cheerUp(weeklyReport.getCheerUp())
                .build();
    }

    public static WeeklyReportResponseDto from(WeeklyReport weeklyReport) {
        WeeklyDataManager manager = new WeeklyDataManager(weeklyReport.getStartDate());

        return WeeklyReportResponseDto.builder()
                .weekOfYear(weeklyReport.getWeekOfYear())
                .week_name(manager.getMonthValue() + "월 " + manager.getWeekOfMonth() + "주차")
                .startDate(weeklyReport.getStartDate())
                .endDate(weeklyReport.getEndDate())
                .frequency(WeeklyFrequencyDto.of(weeklyReport.getPublishedCount(), weeklyReport.getUnpublishedCount()))
                // TODO: 요일별 추이
                .cheerUp(weeklyReport.getCheerUp())
                .build();
    }
}
