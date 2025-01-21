package site.radio.report.weekly.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.radio.clova.dailyReport.ClovaDailyAnalysisResult;
import site.radio.clova.dailyReport.DailyReportMessageParser;
import site.radio.clova.dto.ClovaResponseDto;
import site.radio.clova.service.ClovaService;
import site.radio.clova.weekly.ClovaWeeklyReportRequestDto;
import site.radio.error.ClovaErrorException;
import site.radio.letter.Letter;
import site.radio.letter.LetterService;
import site.radio.report.daily.domain.DailyReport;
import site.radio.report.daily.service.DailyReportService;
import site.radio.report.weekly.dto.WeeklyReportResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyFacade {

    private final DailyReportService dailyReportService;
    private final WeeklyReportService weeklyReportService;
    private final ClovaService clovaService;
    private final LetterService letterService;

    public WeeklyReportResponseDto createWeeklyReport(UUID userId, LocalDate startDate) {
        // 주간 리포트 생성 여부 확인
        weeklyReportService.vaildateWeeklyReportBy(userId, startDate);

        // 클로바에게 요청을 보낼 1주일치 편지 조회
        Map<LocalDate, List<Letter>> lettersForDailyReport = letterService.findLettersForDailyReport(
                userId, startDate, startDate.plusDays(6));

        // 일일 리포트를 만들 편지들을 Clova에게 전송
        Map<CompletableFuture<ClovaDailyAnalysisResult>, List<Letter>> futuresToLetters = sendAsyncLettersForDailyReport(lettersForDailyReport);

        // 비동기 결과 기다리기
        Map<ClovaDailyAnalysisResult, List<Letter>> analysisResultToLetters = resolveDailyReportFutures(futuresToLetters);

        // 클로바로부터 받은 일일 리포트의 분석결과들을 저장
        dailyReportService.saveClovaDailyAnalysisResult(analysisResultToLetters);

        // 주간 분석에 필요한 위로 한마디 구하기
        List<LocalDate> oneWeek = createOneWeek(startDate);
        List<DailyReport> dailyReports = dailyReportService.getDailyReportsOfOneWeek(userId, oneWeek);
        ClovaWeeklyReportRequestDto clovaWeeklyReportRequestDto = ClovaWeeklyReportRequestDto.from(
                combineDescription(dailyReports));

        // 주간 분석 요청 및 응답
        ClovaResponseDto clovaResponseDto = clovaService.sendWeeklyReportRequest(clovaWeeklyReportRequestDto);
        String resultMessage = clovaResponseDto.getResultMessage();

        return weeklyReportService.createWeeklyReport(userId, oneWeek, resultMessage);
    }

    private Map<ClovaDailyAnalysisResult, List<Letter>> resolveDailyReportFutures(
            Map<CompletableFuture<ClovaDailyAnalysisResult>, List<Letter>> lettersByFutures) {
        return lettersByFutures.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().join(),
                        Entry::getValue
                ));
    }

    private Map<CompletableFuture<ClovaDailyAnalysisResult>, List<Letter>> sendAsyncLettersForDailyReport(
            Map<LocalDate, List<Letter>> lettersForDailyReport) {
        return lettersForDailyReport.values()
                .stream()
                .collect(Collectors.toMap(
                        letters -> clovaService.sendAsyncDailyReportRequest(letters)
                                .thenApply(DailyReportMessageParser::extract)
                                .exceptionally(ex -> {
                                    throw new ClovaErrorException("Clova 비동기 중 오류 발생" + Thread.currentThread().getName());
                                }),
                        letters -> letters
                ));
    }

    private List<LocalDate> createOneWeek(LocalDate startDate) {
        return IntStream.range(0, 7)
                .mapToObj(startDate::plusDays)
                .toList();
    }

    private String combineDescription(List<DailyReport> dailyReports) {
        return dailyReports.stream()
                .map(DailyReport::getDescription)
                .collect(Collectors.joining());
    }
}
