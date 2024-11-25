package bsise.server.report.daily.service;

import bsise.server.auth.OAuth2Provider;
import bsise.server.letter.Letter;
import bsise.server.letter.LetterRepository;
import bsise.server.report.daily.dto.DailyReportDto.CreateRequest;
import bsise.server.report.daily.dto.DailyReportResponseDto;
import bsise.server.user.domain.Preference;
import bsise.server.user.domain.Role;
import bsise.server.user.domain.User;
import bsise.server.user.repository.UserRepository;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class DailyReportServiceTest {

    @Autowired
    private DailyReportService dailyReportService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LetterRepository letterRepository;

    @DisplayName("외부 API 연동을 대체하는 더미 응답으로 데일리 리포트를 생성할 수 있다.")
    @Test
    @Transactional
    void createDummyDailyReport() throws NoSuchFieldException, IllegalAccessException {
        User user = createTestUser();
        LocalDate localDate = LocalDate.of(2024, 10, 1);
        Letter letter1 = createLetterByLocalDate(user, localDate);
        Letter letter2 = createLetterByLocalDate(user, localDate);

        // given
        CreateRequest dto = CreateRequest.builder()
                .userId(user.getId().toString())
                .date(localDate)
                .build();

        // when
        DailyReportResponseDto dailyReport = dailyReportService.createDailyReport(dto);

        // then
        Assertions.assertThat(dailyReport.getDate()).isEqualTo(localDate);
    }

    private User createTestUser() {
        User user = User.builder()
                .username("tester1")
                .nickname("tester1")
                .email("test")
                .preference(Preference.F)
                .provider(OAuth2Provider.KAKAO)
                .role(Role.OAUTH)
                .build();

        return userRepository.save(user);
    }

    private @NotNull Letter createLetterByLocalDate(User user, LocalDate localDate)
            throws NoSuchFieldException, IllegalAccessException {
        Letter letter = Letter.builder()
                .user(user)
                .build();

        letterRepository.save(letter);

        // superClass => getDeclaredField() 사용
        Field createdAt = Letter.class.getSuperclass().getDeclaredField("createdAt");
        createdAt.setAccessible(true);
        createdAt.set(letter, LocalDateTime.of(localDate, LocalTime.MIN));

        letterRepository.saveAndFlush(letter);

        return letter;
    }
}