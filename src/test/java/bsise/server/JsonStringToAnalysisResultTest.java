package bsise.server;

import bsise.server.clovar.dailyReport.AnalysisResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonStringToAnalysisResultTest {

    @Test
    void readValue_validResponse_success() throws JsonProcessingException {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();

        String validResponseJson = """
                {
                    "letterAnalyses": [
                        {
                            "coreEmotions": ["슬픔", "분노"],
                            "sensitiveEmotions": [],
                            "topic": "과도한 학업에의 부담"
                        },
                        {
                            "coreEmotions": ["중립"],
                            "sensitiveEmotions": [],
                            "topic": "식사 기록"
                        }
                    ],
                    "dailyCoreEmotion": "슬픔",
                    "description": "공부와 코딩에 몰두하느라 바쁜 하루를 보내면서도 약간의 피로감을 느끼고 계시는군요."
                }
                """;

        // When
        AnalysisResult analysisResult = objectMapper.readValue(validResponseJson, AnalysisResult.class);

        // Then
        Assertions.assertThat(analysisResult).isNotNull();
        Assertions.assertThat(analysisResult.getDailyCoreEmotion()).isEqualTo("슬픔");
        Assertions.assertThat(analysisResult.getLetterAnalyses()).hasSize(2);

        // 첫 번째 letterAnalyses 검증
        Assertions.assertThat(analysisResult.getLetterAnalyses().get(0).getCoreEmotions())
                .containsExactlyInAnyOrder("슬픔", "분노");
        Assertions.assertThat(analysisResult.getLetterAnalyses().get(0).getTopic())
                .isEqualTo("과도한 학업에의 부담");

        // 두 번째 letterAnalyses 검증
        Assertions.assertThat(analysisResult.getLetterAnalyses().get(1).getCoreEmotions())
                .containsExactly("중립");
        Assertions.assertThat(analysisResult.getLetterAnalyses().get(1).getTopic())
                .isEqualTo("식사 기록");
    }
}
