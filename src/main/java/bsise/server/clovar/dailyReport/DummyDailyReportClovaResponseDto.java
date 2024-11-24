package bsise.server.clovar.dailyReport;

import bsise.server.clovar.ClovaResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DummyDailyReportClovaResponseDto extends ClovaResponseDto {

    private final int letterCount;

    @Override
    public String getResultMessage() {
        if (letterCount == 1) {
            return """
                {
                    "letterAnalyses": [
                        {
                            "coreEmotions": ["슬픔"],
                            "sensitiveEmotions": ["우울함"],
                            "topic": "비 오는 날의 독서"
                        }
                    ],
                    "dailyCoreEmotion": "슬픔",
                    "description": "당신."
                }
                """;
        }
        if (letterCount == 2) {
            return """
                {
                    "letterAnalyses": [
                        {
                            "coreEmotions": ["슬픔"],
                            "sensitiveEmotions": ["우울함"],
                            "topic": "비 오는 날의 독서"
                        },
                        {
                            "coreEmotions": ["기쁨", "슬픔"],
                            "sensitiveEmotions": ["고민"],
                            "topic": "친구들과의 모임과 미래 고민"
                        }
                    ],
                    "dailyCoreEmotion": "슬픔",
                    "description": "당신."
                }
                """;
        }
        return """
                {
                    "letterAnalyses": [
                        {
                            "coreEmotions": ["슬픔"],
                            "sensitiveEmotions": ["우울함"],
                            "topic": "비 오는 날의 독서"
                        },
                        {
                            "coreEmotions": ["기쁨", "슬픔"],
                            "sensitiveEmotions": ["고민"],
                            "topic": "친구들과의 모임과 미래 고민"
                        },
                        {
                            "coreEmotions": ["분노"],
                            "sensitiveEmotions": ["무례함"],
                            "topic": "지하철에서의 목격"
                        }
                    ],
                    "dailyCoreEmotion": "슬픔",
                    "description": "당신."
                }
                """;
    }

    public static ClovaResponseDto createDummy(int letterCount) {
        return new DummyDailyReportClovaResponseDto(letterCount);
    }
}
