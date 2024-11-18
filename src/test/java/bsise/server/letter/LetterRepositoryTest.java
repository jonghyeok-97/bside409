package bsise.server.letter;

import static org.assertj.core.api.Assertions.assertThat;

import bsise.server.auth.OAuth2Provider;
import bsise.server.report.CoreEmotion;
import bsise.server.report.DailyReport;
import bsise.server.report.DailyReportRepository;
import bsise.server.user.domain.Preference;
import bsise.server.user.domain.Role;
import bsise.server.user.domain.User;
import bsise.server.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class LetterRepositoryTest {

    @Autowired
    private LetterRepository letterRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyReportRepository dailyReportRepository;

    @AfterEach
    void tearDown() {
        letterRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        dailyReportRepository.deleteAllInBatch();
    }

    @DisplayName("유저 ID와 편지 생성 날짜를 가지고, 지정된 날짜부터 7일 동안 일일 분석이 누락된 편지들을 조회한다")
    @Test
    void test1() {
        // given
        User user1 = saveUser("user1@naver.com");
        User user2 = saveUser("user2@naver.com");

        Letter letter1 = createLetter(user1, LocalDate.of(2024, 11, 16));
        Letter letter2 = createLetter(user1, LocalDate.of(2024, 11, 16));
        Letter letter3 = createLetter(user1, LocalDate.of(2024, 11, 18));
        Letter letter4 = createLetter(user1, LocalDate.of(2024, 11, 17));
        Letter letter5 = createLetter(user1, LocalDate.of(2024, 11, 17));
        letter5.setDailyReport(saveDailyReport());

        Letter letter6 = createLetter(user2, LocalDate.of(2024, 11, 16));
        letterRepository.saveAll(List.of(letter1, letter2, letter3, letter4, letter5, letter6));

        // when
        LocalDateTime start = LocalDate.of(2024, 11, 16).atStartOfDay();
        LocalDateTime end = start.plusDays(7);

        List<Letter> letters = letterRepository.findLettersByDailyReportIsNullAndUserIdAndCreatedAtBetween(
                user1.getId(),
                start,
                end);

        // then
        assertThat(letters).containsExactlyInAnyOrder(
                letter1, letter2, letter3, letter4
        );
    }

    private Letter createLetter(User user, LocalDate target) {
        return Letter.builder()
                .user(user)
                .build();
    }

    private User saveUser(String email) {
        User user = User.builder()
                .nickname("닉네임")
                .email(email)
                .username("유저이름")
                .preference(Preference.T)
                .provider(OAuth2Provider.KAKAO)
                .role(Role.OAUTH)
                .build();
        return userRepository.save(user);
    }

    private DailyReport saveDailyReport() {
        DailyReport dailyReport = DailyReport.builder()
                .description("설명")
                .coreEmotion(CoreEmotion.기쁨)
                .targetDate(LocalDate.of(2024, 11, 8))
                .build();

        return dailyReportRepository.save(dailyReport);
    }
}