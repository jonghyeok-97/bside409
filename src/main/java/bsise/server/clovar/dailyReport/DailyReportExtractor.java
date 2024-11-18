package bsise.server.clovar.dailyReport;

import bsise.server.clovar.ClovaResponseDto;
import bsise.server.report.CoreEmotion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class DailyReportExtractor {

    public static AnalysisResult extract(ClovaResponseDto response) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            AnalysisResult analysisResult = objectMapper.readValue(response.getResultMessage(), AnalysisResult.class);

            // 감정 목록에 없는 감정 필터링
            Set<String> invalidEmotions = analysisResult.getLetterAnalyses().stream()
                    .flatMap(analysis -> analysis.getCoreEmotions().stream())
                    .filter(CoreEmotion::isAbsent)
                    .collect(Collectors.toSet());

            // 일일 대표 감정 검증
            boolean dailyCoreEmotionInvalid = CoreEmotion.isAbsent(analysisResult.getDailyCoreEmotion());

            if (!invalidEmotions.isEmpty() || dailyCoreEmotionInvalid) {
                throw new IllegalArgumentException("대표 감정 목록에 존재하지 않는 감정이 분석되었습니다: " + invalidEmotions);
            }

            return analysisResult;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("답변 형식이 잘못되었습니다. 답변받은 데이터: " + response.getResultMessage(), e);
        }
    }
}

