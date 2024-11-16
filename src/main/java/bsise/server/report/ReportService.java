package bsise.server.report;

import bsise.server.error.DuplicateDailyReportException;
import bsise.server.error.LetterNotFoundException;
import bsise.server.letter.Letter;
import bsise.server.letter.LetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final DailyReportRepository dailyReportRepository;
    private final LetterRepository letterRepository;

    /**
     * <ol> 이 메서드는 순차대로 아래 작업을 수행합니다.
     *     <li>전달받은 유저 아이디와 대상 날짜에 해당하는 일일 리포트가 이미 존재하는지 확인합니다.</li>
     *     <li>가장 최근 편지 3개를 찾아 분석합니다. 오늘이라면 현재 시점 기준, 오늘 이전이라면 해당 날짜 기준 가장 최근 편지 3개를 조회합니다.</li>
     *     <li>클로바에게 조회된 편지로 일일 리포트를 생성을 요청합니다.</li>
     *     <li>분석된 일일 리포트를 저장하고 응답합니다.</li>
     * </ol>
     *
     * @param dailyReportDto 일일 리포트 생성 요청 DTO
     * @return 생성된 일일 리포트에 대한 응답 DTO
     */
    public DailyReportResponseDto createDailyReport(DailyReportRequestDto dailyReportDto) {
        // 해당 날짜 일일 리포트 존재 확인
        if (dailyReportRepository.existsByUserAndTargetDate(UUID.fromString(dailyReportDto.getUserId()), dailyReportDto.getDate())) {
            throw new DuplicateDailyReportException("Duplicate daily report exists.");
        }

        // 가장 최근 편지 3개 조회
        LocalDateTime endTime;
        if(dailyReportDto.getDate().isEqual(LocalDate.now())) { // 오늘이면 현재 시점 기준
            endTime = LocalDateTime.now();
        } else {    // 오늘보다 과거면 해당 날짜 기준 가장 최근
            endTime = dailyReportDto.getDate().atTime(23, 59, 59, 999_999_999);
        }

        List<Letter> letters = letterRepository.find3RecentLetters(
                UUID.fromString(dailyReportDto.getUserId()),
                dailyReportDto.getDate().atStartOfDay(), endTime);

        // 조회된 편지 존재 확인
        if(letters.isEmpty()) {
            throw new LetterNotFoundException("Letters not found.");
        }

        // TODO: 클로바 요청, 응답
        return new DailyReportResponseDto();
    }
}
