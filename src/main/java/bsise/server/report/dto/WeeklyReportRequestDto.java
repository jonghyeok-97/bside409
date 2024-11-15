package bsise.server.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Schema(description = "주간 분석 요청 DTO")
@Getter
@RequiredArgsConstructor
public class WeeklyReportRequestDto {

    @Schema(description = "유저의 아이디", requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "유저의 아이디가 존재하지 않습니다.")
    private final UUID userId;

    @Schema(description = "유저가 주간 분석을 받을 주차",
            minimum = "1",
            maximum = "53",
            requiredMode = RequiredMode.REQUIRED)
    @Range(min = 1, max = 53, message = "주간 분석을 받을 주차를 선택해 주세요.")
    private final int week;
}
