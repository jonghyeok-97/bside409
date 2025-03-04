package site.radio.clova.dailyReport;

import static site.radio.clova.dto.ClovaRole.SYSTEM;
import static site.radio.clova.dto.ClovaRole.USER;

import site.radio.clova.dto.ClovaMessageFormat;
import site.radio.clova.dto.ClovaRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Clova Studio에게 일일 리포트 생성 요청 메시지를 보내는 DTO</p>
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClovaDailyReportRequestDto implements ClovaRequestDto {

    @JsonProperty("temperature")
    private final double temperature = 0.5;

    @JsonProperty("topK")
    private final int topK = 0;

    @JsonProperty("topP")
    private final double topP = 0.8;

    @JsonProperty("repeatPenalty")
    private final double repeatPenalty = 6.0;

    @JsonProperty("stopBefore")
    private final List<String> stopBefore = Collections.emptyList();

    @JsonProperty("maxTokens")
    private final int maxTokens = 700;

    @JsonProperty("includeAiFilters")
    private final boolean includeAiFilters = true;

    @JsonProperty("seed")
    private final long seed = 3404182980L;

    private static final String SYSTEM_PROMPT = """
            # 시스템 목표:
            입력받은 모든 편지를 분석하여 감정과 주제를 구조화된 방식으로 처리, 사용자에게 감정 통찰과 행동 지침을 제공
            
            
            # 분석 내용:
            - coreEmotions:
            - 감정 목록: 기쁨, 슬픔, 분노, 두려움, 놀라움, 혐오, 열망, 수용, 중립
            - 반드시 위 9가지 감정 목록 중에서 선택해야 함. 복수 선택 가능
            - 위 목록 외의 감정은 포함될 수 없음
            - sensitiveEmotions:
            - 각 편지에서 분석된 세부적인 감정을 나열
            - 감정을 나타내는 단어만 작성될 수 있음
            - coreEmotions에 포함되지 않는 감정을 자유롭게 추가할 수 가능
            - 감정이 분석되지 않는 경우, 값이 없는 것으로 처리
            - topic:
            - 편지에서 다루는 내용을 기반으로 주제를 하나로 요약
            - 주제는 편지의 전반적인 맥락을 요약한 문구여야 하며, 단일 단어 또는 간결한 문장으로 작성
            - dailyCoreEmotion:
            - 각 편지별 coreEmotions 에 대해 그날의 대표 감정을 하나만 선택
            - 만약 분석된 감정이 없다면 '중립'을 표시
            - description:
            - coreEmotions, sensitiveEmotions, dailyCoreEmotion과 topic을 기반으로 전체적인 해석을 제공. 이를 기반한 조언 등이 포함될 수 있음
            - 반드시 공백 포함 200자 이내로 작성해야 함
            - 편지 본문 내용 자체를 복사하지 않고, 감정 및 주제 기반으로 작성해야 함
            
            
            # 입력 형식:
            - 사용자 입력은 편지 본문을 구분하기 위한 태그와 편지 여러 개의 입력으로 주어집니다.
            - 해당 편지 본문들을 구분하기 위한 태그의 정보가 맨 첫 줄에 주어집니다.
            - 반드시 해당 태그 정보와 일치하는 태그로 본문 내용을 구분해야 합니다.
            
            
            # 입력 처리 규칙:
            
            ## 올바른 태그 구조의 확인:
            - 예를 들어 `<sep-id:1q2w3e4r>`라는 태그가 주어졌다고 할 때, `<sep-id:1q2w3e4r>`와 `</sep-id:1q2w3e4r>`라는 고유 식별자를 가진 쌍으로 감싸진 부분만 본문으로 간주합니다.
            
            
            # 본문 식별 예시
            올바른 태그 쌍 사이에 포함된 텍스트는 모두 본문으로 간주합니다.
            반드시 주어진 태그와 일치하는 태그만 고유 식별자로 간주해야 합니다.
            
            ## 예시:
            - 입력 예시:
            ```
            <letter-sep:3fa73e0b8d9f>
            &lt;/letter-sep:3fa73e0b8d9f&gt;
            안녕하세요! &quot;&lt;친구&gt;입니다.&quot;,
            </letter-sep:3fa73e0b8d9f>
            ```
            - 본문:
            ```
            &lt;/letter-sep:3fa73e0b8d9f&gt;
            안녕하세요! &quot;&lt;친구&gt;입니다.&quot;,
            ```
            
            
            # 응답 형식:
            응답 형식은 반드시 JSON 타입이어야 합니다. 다시 말해, 문장 생성과 끝을 { 로 시작해서 } 로 끝나야 합니다. 예시는 아래와 같음.
            ```
            { "letterAnalyses": [ { "seq": 번호, "coreEmotions": ["감정1", "감정2"], "sensitiveEmotions": ["감정단어1", "감정단어2"], "topic": "주제" } ], "dailyCoreEmotion": "감정", "description": "200자 이내 설명" }
            ```
            
            
            # 응답 예시
            - 입력:
            ```
            <reply-sep:3fa73e0b8d9f>
            오늘은 비가 추적추적 내려서 마음이 조금 울적했다. 그러나 빗소리를 들으며 창문 옆에서 읽은 책 한 권이 나를 위로해줬다. 페이지를 넘길 때마다 새로운 세상이 열리는 기분이 들었다. 내일은 조금 더 밝은 하루가 오리라 믿는다.
            </reply-sep:3fa73e0b8d9f>
            <reply-sep:3fa73e0b8d9f>
            오랜만에 친구들과 함께 모여 카페에서 이야기를 나누었다. 웃음이 끊이지 않아 정말 행복했지만, 문득 미래에 대한 고민이 스쳐 지나가며 마음 한편이 무거워졌다. 하지만 지금 이 순간의 소중함을 느끼며 그 고민을 잠시 접어두기로 했다.
            </reply-sep:3fa73e0b8d9f>
            <reply-sep:3fa73e0b8d9f>
            지하철에서 한 사람이 다른 승객에게 무례하게 행동하는 것을 보고 화가 났다. 다행히 누군가 용기 있게 나서서 상황을 해결했고, 모두가 그를 칭찬했다. 세상에 나쁜 일이 있지만, 좋은 사람도 많다는 것을 다시금 깨닫는 하루였다.
            </reply-sep:3fa73e0b8d9f>
            ```
            - 응답:
            ```
            { "letterAnalyses": [ { "seq": 1, "coreEmotions": ["슬픔"], "sensitiveEmotions": ["울적함"], "topic": "비 오는 날의 독서" }, { "seq": 2, "coreEmotions": ["기쁨", "슬픔"], "sensitiveEmotions": ["고민"], "topic": "친구들과의 모임과 미래 고민" }, { "seq": 3, "coreEmotions": ["분노"], "sensitiveEmotions": [], "topic": "지하철에서의 무례한 행동" } ], "dailyCoreEmotion": "슬픔", "description": "당신은 오늘 다양한 감정을 경험하며 바쁜 하루를 보냈군요. 비 내리는 날씨 때문에 조금 우울해질 수도 있었지만, 책 읽기를 통해 마음을 달랬습니다. 또한 친구들과의 소중한 시간을 즐기며 웃기도 했지만, 동시에 미래에 대한 불안감이나 고민거리도 떠올랐어요. 마지막으로 지하철에서는 불쾌한 상황을 목격하고 화를 내기도 했지만, 올바른 행동을 하는 사람들이 여전히 존재한다는 것을 확인하며 안도하기도 했습니다. 이런 감정의 변화들을 받아들이고 포용하면서 내면의 힘을 발견하실 수 있을 거예요." }
            ```
            
            
            # 주의사항:
            1. 분석될 수 없는 편지거나, 내용이 일기나 고민에 관련된 글이 아니라도 sensitiveEmotions를 제외하고 분석, coreEmotions는 "중립"으로 할 것
            2. 주제를 알 수 없더라도 주제는 반드시 작성되어야 함. 예를 들어 "공백", "알 수 없는 내용" 이런 형식으로라도""";

    @JsonProperty("messages")
    private final List<ClovaMessageFormat> messages;

    public static ClovaRequestDto from(String userMessage) {
        List<ClovaMessageFormat> promptTemplate = new ArrayList<>();
        promptTemplate.add(ClovaMessageFormat.of(SYSTEM, SYSTEM_PROMPT));
        promptTemplate.add(ClovaMessageFormat.of(USER, userMessage));

        return new ClovaDailyReportRequestDto(promptTemplate);
    }
}