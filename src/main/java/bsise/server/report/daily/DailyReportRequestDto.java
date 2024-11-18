package bsise.server.report.daily;

import bsise.server.validation.constraints.WithinLastMonth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class DailyReportRequestDto {

    @NotBlank(message = "유저의 아이디가 존재하지 않습니다.", groups = DailyReportGetRequest.class)
    private final String userId;

    @NotNull(message = "생성 요청 날짜는 필수 요청 값입니다.")
    @WithinLastMonth(message = "생성 요청 날짜는 오늘 포함 한달 이전까지만 가능합니다.", groups = {WithinLastMonth.class})
    private final LocalDate date;
}
