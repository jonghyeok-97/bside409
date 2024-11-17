package bsise.server.report;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyReportResponseDto {

    private final LocalDate date;
    private final List<LetterAnalysisResult> letterAnalyses;
    private final String dailyCoreEmotion;
    private final String description;

    @Getter(AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LetterAnalysisResult {
        private final UUID letterId;
        private final List<String> coreEmotions;
        private final List<String> sensitiveEmotions;
        private final String topic;
        private final LocalDateTime createdAt;

        public static LetterAnalysisResult of(LetterAnalysis letterAnalysis) {
            return LetterAnalysisResult.builder()
                    .letterId(letterAnalysis.getLetter().getId())
                    .coreEmotions(letterAnalysis.getCoreEmotions().stream().map(Enum::name).collect(Collectors.toList()))
                    .sensitiveEmotions(letterAnalysis.getSensitiveEmotions())
                    .topic(letterAnalysis.getTopic())
                    .createdAt(letterAnalysis.getCreatedAt())
                    .build();
        }
    }

    public static DailyReportResponseDto of(DailyReport dailyReport, List<LetterAnalysis> letterAnalyses) {
        return DailyReportResponseDto.builder()
                .date(dailyReport.getTargetDate())
                .letterAnalyses(letterAnalyses.stream().map(LetterAnalysisResult::of).collect(Collectors.toList()))
                .dailyCoreEmotion(dailyReport.getCoreEmotion().name())
                .description(dailyReport.getDescription())
                .build();
    }
}
