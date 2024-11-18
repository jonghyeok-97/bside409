package bsise.server.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class DailyReportRepositoryTest {

    @Autowired
    private DailyReportRepository dailyReportRepository;

    @AfterEach
    void tearDown() {
        dailyReportRepository.deleteAllInBatch();
    }

    @DisplayName("시작날짜로부터 1주일간 생성된 일일분석들을 찾는다")
    @Test
    void test() {
        // given
        DailyReport dailyReport1 = DailyReport.builder()
                .coreEmotion(CoreEmotion.기쁨)
                .targetDate(LocalDate.of(2024, 11, 13))
                .description("해석1")
                .build();
        DailyReport dailyReport2 = DailyReport.builder()
                .coreEmotion(CoreEmotion.분노)
                .targetDate(LocalDate.of(2024, 11, 15))
                .description("해석2")
                .build();
        DailyReport dailyReport3 = DailyReport.builder()
                .coreEmotion(CoreEmotion.놀라움)
                .targetDate(LocalDate.of(2024, 11, 18))
                .description("해석3")
                .build();
        dailyReportRepository.saveAll(List.of(dailyReport1, dailyReport2, dailyReport3));

        LocalDate startDate = LocalDate.of(2024, 11, 11);

        // when
        List<LocalDate> oneWeekDates = IntStream.range(0, 7)
                .mapToObj(startDate::plusDays)
                .collect(Collectors.toList());

        List<DailyReport> reports = dailyReportRepository.findDailyReportsByTargetDateIn(oneWeekDates);

        // thn
        assertThat(reports).hasSize(2);
        assertThat(reports).extracting("targetDate").containsAnyOf(
                LocalDate.of(2024,11,13),
                LocalDate.of(2024,11,15)
        );
    }
}