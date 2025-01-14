package site.radio.report.weekly.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.radio.clova.dailyReport.ClovaDailyAnalysisResult;
import site.radio.clova.dailyReport.DailyReportExtractor;
import site.radio.clova.dailyReport.DailyReportPreprocessor;
import site.radio.clova.dto.ClovaResponseDto;
import site.radio.clova.service.ClovaService;
import site.radio.letter.Letter;
import site.radio.letter.LetterService;
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

        // 클로바에게 요청을 보낼 사용자의 1주일치 편지들 찾기
        Map<LocalDate, List<Letter>> lettersForDailyReport = letterService.findLettersForDailyReport(
                userId, startDate, startDate.plusDays(6));

        // 일일 리포트의 편지들을 가공하고, 클로바에게 요청
        Map<ClovaDailyAnalysisResult, List<Letter>> lettersByAnalysisResult = lettersForDailyReport.values().stream()
                .collect(Collectors.toMap(
                        letters -> {
                            String combinedLetters = DailyReportPreprocessor.requestClovaAnalysis(letters);
                            ClovaResponseDto clovaResponseDto = clovaService.sendDailyReportRequest(combinedLetters);
                            return DailyReportExtractor.extract(clovaResponseDto);
                        },
                        letters -> letters
                ));

        // 클로바로부터 받은 일일 리포트의 분석결과들을 저장
        dailyReportService.saveClovaDailyAnalysisResult(lettersByAnalysisResult);

        return null;
    }
}
