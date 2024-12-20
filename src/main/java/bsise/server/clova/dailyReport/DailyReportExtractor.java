package bsise.server.clova.dailyReport;

import bsise.server.clova.dto.ClovaResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DailyReportExtractor {

    public static ClovaDailyAnalysisResult extract(ClovaResponseDto response) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(response.getResultMessage(), ClovaDailyAnalysisResult.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("답변 형식이 잘못되었습니다. 답변받은 데이터: " + response.getResultMessage(), e);
        }
    }
}

