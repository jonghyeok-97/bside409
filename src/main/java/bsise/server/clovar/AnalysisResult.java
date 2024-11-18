package bsise.server.clovar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisResult {

    @JsonProperty(value = "letterAnalyses")
    private List<LetterAnalysis> letterAnalyses;

    @JsonProperty(value = "dailyCoreEmotion")
    private String dailyCoreEmotion;

    @JsonProperty(value = "description")
    private String description;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LetterAnalysis {

        @JsonProperty(value = "coreEmotions")
        private List<String> coreEmotions;

        @JsonProperty(value = "sensitiveEmotions")
        private List<String> sensitiveEmotions;

        @JsonProperty(value = "topic")
        private String topic;
    }
}