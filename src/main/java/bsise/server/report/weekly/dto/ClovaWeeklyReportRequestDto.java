package bsise.server.report.weekly.dto;

import static bsise.server.clova.dto.ClovaRole.ASSISTANT;
import static bsise.server.clova.dto.ClovaRole.SYSTEM;
import static bsise.server.clova.dto.ClovaRole.USER;

import bsise.server.clova.dto.ClovaMessageFormat;
import bsise.server.clova.dto.ClovaRequestDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClovaWeeklyReportRequestDto implements ClovaRequestDto {

    private List<ClovaMessageFormat> messages;

    private final Double temperature = 0.5;
    private final Integer topK = 0;
    private final Double topP = 0.8;
    private final Double repeatPenalty = 6.0;
    private final List<String> stopBefore = new ArrayList<>();
    private final Integer maxTokens = 700;
    private final boolean includeAiFilters = true;
    private final Integer seed = 0;

    private static final String MESSAGE_DELIMETER = "===";

    // TODO: 클로바템플릿작성하기
    private static final String SYSTEM_PROMPT = " ";
    private static final String USER_PROMPT = "";
    private static final String ASSISTANT_RESPONSE1 = "";
    private static final String ASSISTANT_PROMPT1 = "";
    private static final String ASSISTANT_RESPONSE2 = "";
    private static final String ASSISTANT_PROMPT2 = "";
    private static final String ASSISTANT_RESPONSE3 = "";

    private static final List<ClovaMessageFormat> PROMPT_TEMPLATE = List.of(
            ClovaMessageFormat.of(SYSTEM, SYSTEM_PROMPT),
            ClovaMessageFormat.of(USER, USER_PROMPT),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_RESPONSE1),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_PROMPT1),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_RESPONSE2),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_PROMPT2),
            ClovaMessageFormat.of(ASSISTANT, ASSISTANT_RESPONSE3)
    );

    public static ClovaWeeklyReportRequestDto from(String descriptions, String coreEmotions) {
        List<ClovaMessageFormat> promptTemplate = promptTemplate();
        promptTemplate.add(ClovaMessageFormat.of(USER, descriptions + MESSAGE_DELIMETER + coreEmotions));

        return new ClovaWeeklyReportRequestDto(promptTemplate);
    }

    private static List<ClovaMessageFormat> promptTemplate() {
        return new ArrayList<>(PROMPT_TEMPLATE);
    }
}
