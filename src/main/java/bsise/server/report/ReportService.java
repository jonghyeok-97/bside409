package bsise.server.report;

import bsise.server.clovar.dailyReport.AnalysisResult;
import bsise.server.clovar.ClovaResponseDto;
import bsise.server.clovar.ClovaService;
import bsise.server.clovar.dailyReport.DailyReportExtractor;
import bsise.server.error.DailyReportNotFoundException;
import bsise.server.error.DuplicateDailyReportException;
import bsise.server.error.LetterNotFoundException;
import bsise.server.letter.Letter;
import bsise.server.letter.LetterRepository;
import bsise.server.report.daily.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final DailyReportRepository dailyReportRepository;
    private final LetterRepository letterRepository;
    private final LetterAnalysisRepository letterAnalysisRepository;
    private final ClovaService clovaService;

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
        UUID userId = UUID.fromString(dailyReportDto.getUserId());
        LocalDate targetDate = dailyReportDto.getDate();

        if (dailyReportRepository.existsByUserAndTargetDate(userId, targetDate)) {
            throw new DuplicateDailyReportException("Duplicate daily report exists.");
        }

        List<Letter> letters = findRecentLetters(userId, targetDate);

        ClovaResponseDto clovaResponse = requestClovaAnalysis(letters);

        AnalysisResult analysisResult = DailyReportExtractor.extract(clovaResponse);
        DailyReport dailyReport = buildDailyReport(targetDate, analysisResult);
        dailyReportRepository.save(dailyReport);

        List<LetterAnalysis> letterAnalyses = buildLetterAnalyses(letters, analysisResult);
        letterAnalyses.forEach(analysis -> analysis.getLetter().setDailyReport(dailyReport));
        letterAnalysisRepository.saveAll(letterAnalyses);

        return DailyReportResponseDto.of(dailyReport, letterAnalyses);
    }

    public DailyReportResponseDto getDailyReport(String userId, LocalDate targetDate) {
        DailyReport dailyReport = dailyReportRepository.findByUserAndTargetDate(UUID.fromString(userId), targetDate)
                .orElseThrow(() -> new DailyReportNotFoundException("Daily Report not found. targetDate: " + targetDate));

        List<LetterAnalysis> letterAnalyses = letterAnalysisRepository.findByDailyReportId(dailyReport.getId());

        return DailyReportResponseDto.of(dailyReport, letterAnalyses);
    }

    /**
     * <ol> userId와 targetDate에 해당하는 편지를 최대 3건 조회합니다.
     *     <li>만약 오늘이라면, 현재 시점 기준 가장 최근 3건</li>
     *     <li>만약 오늘 이전이라면, 해당 날짜의 가장 마지막 3건</li>
     * </ol>
     * @param userId 일일분석 요청하는 사용자 아이디
     * @param targetDate 일일분석 요청할 날짜
     * @return 편지 3건 리스트
     */
    private List<Letter> findRecentLetters(UUID userId, LocalDate targetDate) {
        LocalDateTime endTime = targetDate.isEqual(LocalDate.now())
                ? LocalDateTime.now()
                : targetDate.atTime(23, 59, 59, 999_999_999);

        List<Letter> letters = letterRepository.find3RecentLetters(
                userId, targetDate.atStartOfDay(), endTime);

        if (letters.isEmpty()) {
            throw new LetterNotFoundException("Letters for daily analysis not found.");
        }
        return letters;
    }

    private ClovaResponseDto requestClovaAnalysis(List<Letter> letters) {
        String formattedMessages = letters.stream()
                .map(Letter::getMessage)
                .map(msg -> "\"" + msg + "\"")
                .collect(Collectors.joining(", "));

        return clovaService.sendDailyReportRequest(formattedMessages);
    }

    private DailyReport buildDailyReport(LocalDate targetDate, AnalysisResult analysisResult) {
        return DailyReport.builder()
                .targetDate(targetDate)
                .coreEmotion(CoreEmotion.valueOf(analysisResult.getDailyCoreEmotion()))
                .description(analysisResult.getDescription())
                .build();
    }

    private List<LetterAnalysis> buildLetterAnalyses(List<Letter> letters, AnalysisResult analysisResult) {
        return analysisResult.getLetterAnalyses().stream()
                .map(analysis -> {
                    int index = analysisResult.getLetterAnalyses().indexOf(analysis);
                    Letter letter = letters.get(index); // 순서대로 letter 매핑

                    return LetterAnalysis.builder()
                            .letter(letter)
                            .topic(analysis.getTopic())
                            .coreEmotions(analysis.getCoreEmotions().stream()
                                    .map(CoreEmotion::valueOf)
                                    .collect(Collectors.toList()))
                            .sensitiveEmotions(analysis.getSensitiveEmotions())
                            .build();
                })
                .collect(Collectors.toList());
    }
}

