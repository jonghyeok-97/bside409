package bsise.server.report;

import bsise.server.clovar.AnalysisResult;
import bsise.server.clovar.ClovaResponseDto;
import bsise.server.clovar.ClovaService;
import bsise.server.clovar.DailyReportExtractor;
import bsise.server.common.BaseTimeEntity;
import bsise.server.error.DailyReportNotFoundException;
import bsise.server.error.DuplicateDailyReportException;
import bsise.server.error.LetterNotFoundException;
import bsise.server.letter.Letter;
import bsise.server.letter.LetterRepository;
import bsise.server.report.weekly.dto.ClovaWeeklyReportRequestDto;
import bsise.server.report.weekly.dto.WeeklyReportRequestDto;
import bsise.server.report.weekly.dto.WeeklyReportResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final DailyReportRepository dailyReportRepository;
    private final LetterRepository letterRepository;
    private final LetterAnalysisRepository letterAnalysisRepository;
    private final ClovaService clovaService;
    private final WeeklyReportRepository weeklyReportRepository;

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

    /*
    - 분석 날짜, 작성빈도, 요일 ⛧ 감정 변화 추이, 위로 메세지, + 데일리의 각 편지 대표 감정
    - 일요일 자정(00:00)을 넘으면 주간 분석 요청 버튼 활성화 및 주간 분석 요청 가능
     */
    public WeeklyReportResponseDto createWeeklyReport(WeeklyReportRequestDto weeklyReportRequestDto) {
        // dailyReport 가 없는 편지들 찾기
        LocalDateTime start = weeklyReportRequestDto.getStartDate().atStartOfDay();
        LocalDateTime end = start.plusDays(7);

        List<Letter> lettersWithoutDailyReport = letterRepository.findLettersByDailyReportIsNullAndUserIdAndCreatedAtBetween(
                UUID.fromString(weeklyReportRequestDto.getUserId()),
                start,
                end
        );

        // 날짜별로 최신 편지들을 3개씩 묶기
        Map<LocalDate, List<Letter>> latestThreeLettersByDate = lettersWithoutDailyReport.stream()
                .collect(Collectors.groupingBy(
                        letter -> letter.getCreatedAt().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(BaseTimeEntity::getCreatedAt).reversed())
                                        .limit(3)
                                        .toList()
                        )
                ));

        // 편지 3개에 대한 분석을 Clova에게 요청해서 받은 결과물들
        Map<AnalysisResult, List<Letter>> lettersByAnalysisResult = latestThreeLettersByDate.values().stream()
                .collect(Collectors.toMap(
                        letters -> DailyReportExtractor.extract(requestClovaAnalysis(letters)),
                        letters -> letters
                ));

        // 분석결과와 편지들을 가지고 데일리 리포트 생성
        Map<DailyReport, List<Letter>> lettersByDailyReport = lettersByAnalysisResult.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> buildDailyReport(LocalDate.now(), entry.getKey()),
                        Entry::getValue
                ));
        dailyReportRepository.saveAll(lettersByDailyReport.keySet());

        // 편지들에 알맞는 데일리 리포트를 setter 주입
        lettersByDailyReport.forEach((key, value) ->
                value.forEach(
                        letter -> letter.setDailyReport(key)
                ));

        // 편지와 분석결과를 가지고 편지분석엔티티들 생성 및 저장
        List<LetterAnalysis> letterAnalyses = lettersByAnalysisResult.entrySet().stream()
                .flatMap(entry -> buildLetterAnalyses(entry.getValue(), entry.getKey()).stream())
                .toList();
        letterAnalysisRepository.saveAll(letterAnalyses);

        // startDate 로 부터 1주일 날짜 구하기
        List<LocalDate> oneWeekDates = createOneWeek(weeklyReportRequestDto.getStartDate());

        // 1주일에 해당하는 데일리 리포트 찾기
        List<DailyReport> dailyReports = dailyReportRepository.findDailyReportsByTargetDateIn(oneWeekDates);

        //dailyReport 에서 설명 합치기
        String descriptions = dailyReports.stream()
                .map(DailyReport::getDescription)
                .collect(Collectors.joining());

        // dailyReport 에서 감정 합치기
        String coreEmotions = dailyReports.stream()
                .map(DailyReport::getCoreEmotion)
                .map(CoreEmotion::name)
                .collect(Collectors.joining());

        // TODO: DailyReportExtractor 에서 클로바 response 읽기 후 저장
        ClovaResponseDto clovaResponseDto = clovaService.sendWeeklyReport(
                ClovaWeeklyReportRequestDto.from(descriptions, coreEmotions));
        // ----

        WeeklyDataManager manager = new WeeklyDataManager(weeklyReportRequestDto.getStartDate());

        WeeklyReport weeklyReport = WeeklyReport.builder()
                .weekOfYear(manager.getWeekOfWeekBasedYear())
                .startDate(manager.getMondayOfWeek())
                .endDate(manager.getSundayOfWeek())
                .cheerUp("위로한마디")
//                .publishedCount()
//                .unpublishedCount()
                .build();
        weeklyReportRepository.save(weeklyReport);
        dailyReports.forEach(dailyReport -> dailyReport.setWeeklyReport(weeklyReport));

        return WeeklyReportResponseDto.from(weeklyReport, manager);
    }

    private List<LocalDate> createOneWeek(LocalDate startDate) {
        return IntStream.range(0, 7)
                .mapToObj(startDate::plusDays)
                .toList();
    }
}
