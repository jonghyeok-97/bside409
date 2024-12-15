package bsise.server.report.daily.dto;

import bsise.server.report.daily.validation.WithinMonths;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DailyReportDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "유저의 아이디가 존재하지 않습니다.")
        private String userId;

        @NotNull(message = "생성 요청 날짜는 필수 요청 값입니다.")
        @WithinMonths(message = "생성 요청 날짜는 오늘 포함 한달 이전까지만 가능합니다.")
        private LocalDate date;
    }
}
