package bsise.server.report.weekly.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "주간 분석 조회 DTO")
@Getter
@RequiredArgsConstructor
public class WeeklyReportGetRequestDto {

    @Schema(description = "유저의 아이디", requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "유저의 아이디가 존재하지 않습니다.")
    private String userId;
}
