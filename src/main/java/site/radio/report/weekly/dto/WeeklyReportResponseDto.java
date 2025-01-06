package site.radio.report.weekly.dto;

import site.radio.report.daily.domain.CoreEmotion;
import site.radio.report.weekly.domain.WeeklyReport;
import site.radio.report.weekly.service.WeeklyDataManager;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "유저의 주간 분석 요청이 성공하면 받는 응답 DTO")
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyReportResponseDto {

    @Schema(description = "1년 기반의 N주차")
    private int weekOfYear;

    @Schema(description = "N월 N주차")
    private String weekName;

    @Schema(description = "N월 N주차의 시작 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @Schema(description = "N월 N주차의 마지막 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;

    @Schema(description = "데일리 리포트들의 CoreEmotion 배열")
    private List<CoreEmotion> coreEmotions;

    @Schema(description = "주간 분석에 사용된 편지 개수")
    private int published;

    @Schema(description = "주간 분석에 사용된 일기 개수")
    private int unPublished;

    @Schema(description = "위로 한 마디")
    private String cheerUp;

    public static WeeklyReportResponseDto from(WeeklyReport weeklyReport, List<CoreEmotion> coreEmotions) {
        LocalDate target = weeklyReport.getStartDate();

        return WeeklyReportResponseDto.builder()
                .weekOfYear(weeklyReport.getWeekOfYear())
                .weekName(getMonthValue(target) + "월 " + getWeekOfMonth(target) + "주차")
                .startDate(weeklyReport.getStartDate())
                .endDate(weeklyReport.getEndDate())
                .coreEmotions(coreEmotions)
                .published(weeklyReport.getPublishedCount())
                .unPublished(weeklyReport.getUnpublishedCount())
                .cheerUp(weeklyReport.getCheerUp())
                .build();
    }

    private static int getMonthValue(LocalDate target) {
        return new WeeklyDataManager(target).getMonthValue();
    }

    private static int getWeekOfMonth(LocalDate target) {
        return new WeeklyDataManager(target).getWeekOfMonth();
    }
}
