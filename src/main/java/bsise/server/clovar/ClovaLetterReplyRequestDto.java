package bsise.server.clovar;

import static bsise.server.clovar.ClovaRole.*;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Clova Studio 에게 메시지를 보내는 DTO</p>
 * <p>프롬프트 템플릿이 사전에 입력되어 있음</p>
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "CLOVA 전송 DTO")
public class ClovaLetterReplyRequestDto implements ClovaRequestDto {

    @Schema(description = "사용자가 작성한 메시지입니다.")
    private List<ClovaMessageFormat> messages;

    private final Double temperature = 0.5;
    private final Integer topK = 0;
    private final Double topP = 0.8;
    private final Double repeatPenalty = 6.0;
    private final List<String> stopBefore = new ArrayList<>();
    private final Integer maxTokens = 700;
    private final boolean includeAiFilters = true;
    private final Integer seed = 1868385636;

    private static final String SYSTEM_PROMPT = "요구사항:\\n상황 설명:\\n사연자는 친구의 생일 선물을 해줬는데, 그 친구는 사연자의 생일날 선물을 주지 않은 상황.\\n\\nMBTI 유형 반영:\\n위로를 할 때 MBTI 성향을 고려해주세요. F 유형(감정형)과 T 유형(사고형)에 맞는 두 가지 다른 스타일의 위로를 제시해 주세요.\\n\\nF 유형 친구에게 위로:\\n\\n공감과 감정적 지지를 중점으로 위로해 주세요.\\n친구의 노력에 공감하고, 그가 겪고 있는 감정을 인정하는 방식으로 작성해 주세요.\\n포근하고 따뜻한 어투를 사용해 주세요. T 유형 친구에게 위로:\\n\\n논리적이고 문제 해결에 초점을 맞춰 위로해 주세요.\\n친구의 상황을 분석하고, 앞으로 어떻게 개선할 수 있을지 제안해 주세요.\\n이성적이고 현실적인 어투로 작성해 주세요.\\n\\n포맷:\\n\\n각 유형에 맞는 두 가지 답변을 제시해 주세요.\\nF 유형, T 유형 2가지 위로를 구분자(\\'---\\')를 기준으로 나눠서 답변해 주세요.\\n반드시 존댓말로 답변해 주세요.\\n따뜻한 이모티콘(\uD83D\uDC9B, \uD83E\uDD79 등)이나 명확한 표현(예: \\\"더 잘할 수 있어\\\")을 꼭 사용해 주세요.\\n\\n결과 비교 가능성:\\n두 답변을 비교할 수 있게 구체적이고 명확하게 작성해 주세요.\\n\\n답변 예시:\\n\\nF 유형 친구에게 위로:정말 속상했겠어요... 기대했던 만큼 실망도 컸을 거에요. \uD83E\uDD79 하지만 너무 마음 쓰지 마길 바래요. 이번 일로 네가 상처받지 않았으면 좋겠어요. 서로 다른 사람이니까, 모든 걸 똑같이 주고받을 순 없겠죠. 그래도 여전히 좋은 추억들이 많이 있잖아요? 이 일로 인해 멀어지지 말아요! \uD83D\uDC9B---T 유형 친구에게 위로:그런 일이 있었군요. 우선 이런 경우에는 왜 그런 행동을 했는지 이유를 파악해야 해요. \uD83E\uDD14 만약 정말 깜빡해서 준비하지 못한 거라면 다음부터는 미리 알려주는게 어떨까요? 그리고 상대방한테 바라는 게 있다면 솔직하게 말하는 게 좋습니다. 이렇게 하면 불필요한 오해나 갈등을 예방할 수 있을 거에요. 힘내요! \uD83D\uDCAA";
    private static final String USER_PROMPT = "나는 친구 생일 선물을 해줬는데, 친구는 내 생일 선물을 안줬어.";
    private static final String ASSISTANT_RESPONSE1 = "F 유형 친구에게 위로:속상하셨겠네요... 분명 큰 맘 먹고 준비했을 텐데 말이죠. \uD83E\uDD7A 혹시 무슨 사정이 있었던 건 아닐까요? 직접 이야기 나눠보는 건 어때요? 서운함을 털어놓고 나면 오히려 더 가까워질 수도 있답니다. 물론 당장은 힘들겠지만, 시간이 지나면 괜찮아질 거예요. 제가 곁에 있으니까 언제든 얘기해요! \uD83D\uDC95---T 유형 친구에게 위로:이런 상황에서는 원인을 파악하는 게 중요하겠죠. 먼저 친구와 연락을 해서 어떤 이유 때문인지 알아보는 건 어떨까요? 단순히 잊어버렸을 수도 있고, 아니면 경제적인 여유가 없어서 미처 준비하지 못했을 수도 있죠. 결과가 어떻든 일단 대화를 통해 풀어 나가는 것이 좋을 것 같아요. 힘내세요! \uD83D\uDE0A";
    private static final String ASSISTANT_PROMPT1 = "취업 준비 중인데 계속 떨어지고 있어서 자신감이 떨어져요. 어떡하죠?";
    private static final String ASSISTANT_RESPONSE2 = "F 유형 친구에게 위로:많이 힘드시죠? 취업이라는 게 참 쉽지 않은 일이에요. 주변 시선들도 신경 쓰이고, 스스로 작아지는 기분까지 들죠. \uD83D\uDE14 그렇지만 이럴 때일수록 자기 자신을 믿어줘야 해요. 그동안 열심히 달려왔잖아요? 조금만 더 힘을 내봐요. 우리 같이 응원할게요! \uD83D\uDC96---T 유형 친구에게 위로:계속되는 실패로 낙담하신 모습 충분히 이해돼요. 그럴수록 객관적으로 본인을 되돌아 보는건 어떠신가요? 부족한 부분을 보완하면 합격 확률을 높일 수 있습니다. 떨어진 이유를 꼼꼼히 분석하고 강점을 어필할 방법을 찾아보세요. 잘 하실 수 있을 거예요! \uD83D\uDE0A";
    private static final String ASSISTANT_PROMPT2 = "시험 기간인데 공부가 손에 안 잡혀요. 집중력 높이는 방법 없을까요?";
    private static final String ASSISTANT_RESPONSE3 = "F 유형 친구에게 위로:공부가 하기 싫을 때가 있죠. 불안한 마음도 들고요. \uD83D\uDE14 잠시 쉬었다가 다시 시작해 보는 건 어떠세요? 스트레칭을 하거나 좋아하는 음악을 듣는 것도 도움이 될 거예요. 무엇보다 지금 겪는 어려움들은 결국 지나갈 거라는 걸 잊지 마세요. 함께 이겨내 봐요! \uD83C\uDF1F---T 유형 친구에게 위로:집중력이 떨어질 때는 목표를 잘게 쪼개서 작은 단위로 계획을 세워보세요. 예를 들어 오늘 해야 할 분량을 시간대별로 나누는 거죠. 또 적절한 휴식도 필요하니 중간중간 몸을 움직이거나 산책을 하는 것도 좋아요. 환경 조성도 중요한데, 백색소음을 활용하거나 조명을 조절해 보는 건 어떨까요? 효과가 있길 바라요! \uD83D\uDE0A";

    private static final List<ClovaMessageFormat> PROMPT_TEMPLATE = List.of(
            ClovaMessageFormat.of(SYSTEM, SYSTEM_PROMPT),
            ClovaMessageFormat.of(USER, USER_PROMPT),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_RESPONSE1),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_PROMPT1),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_RESPONSE2),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_PROMPT2),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_RESPONSE3)
    );

    public static ClovaRequestDto from(String userMessage) {
        List<ClovaMessageFormat> promptTemplate = promptTemplate();
        promptTemplate.add(ClovaMessageFormat.of(USER, userMessage));

        return new ClovaLetterReplyRequestDto(promptTemplate);
    }

    private static List<ClovaMessageFormat> promptTemplate() {
        return new ArrayList<>(PROMPT_TEMPLATE);
    }
}