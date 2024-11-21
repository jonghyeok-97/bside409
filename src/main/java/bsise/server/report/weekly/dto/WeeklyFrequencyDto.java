package bsise.server.report.weekly.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "주간 분석 응답의 편지 개수와 일기 개수 DTO")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyFrequencyDto {

    //TODO: 기획 확정 시 schema 추가
    private int published;

    private int unpublished;

    /**
     * @param published : 편지 개수
     * @param unpublished : 일기 개수
     */
    public static WeeklyFrequencyDto of(int published, int unpublished) {
        return new WeeklyFrequencyDto(published, unpublished);
    }
}
