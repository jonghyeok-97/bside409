package bsise.server.dummy;

import static bsise.server.user.domain.Preference.F;
import static bsise.server.user.domain.Preference.T;

import bsise.server.clovar.ClovaFeignClient;
import bsise.server.clovar.ClovaLetterReplyRequestDto;
import bsise.server.clovar.ClovaRequestDto;
import bsise.server.clovar.ClovaResponseDto;
import bsise.server.clovar.MessageExtractor;
import bsise.server.clovar.TwoTypeMessage;
import bsise.server.report.weekly.dto.ClovaWeeklyReportRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClovaDummyService {

    @Value("${clova.api.key}")
    private String apiKey;

    @Value("${clova.apigw.key}")
    private String apigwKey;

    @Value("${clova.request.id}")
    private String requestId;

    private final ClovaFeignClient client;

    public ClovaResponseDto send(String message) {
        return sendRequestToClova(ClovaLetterReplyRequestDto.from(message));
    }

    public ClovaResponseDto sendDailyReportRequest(String message) {
        return createDummyResponse();
    }

    public ClovaResponseDto sendWeeklyReportRequest(ClovaWeeklyReportRequestDto dto) {
        return createDummyResponse();
    }

    private ClovaResponseDto sendRequestToClova(ClovaRequestDto clovaRequestDto) {
        ClovaResponseDto response = client.sendToClova(apiKey, apigwKey, requestId, clovaRequestDto);

        if (response.hasErrorCode()) {
            throw new IllegalStateException("클로바 응답에 문제가 생겼습니다. 잠시 후 다시 시도하세요.");
        }

        return response;
    }

    public TwoTypeMessage extract(ClovaResponseDto response) {
        String messageF = MessageExtractor.extract(response.getResultMessage(), F);
        String messageT = MessageExtractor.extract(response.getResultMessage(), T);

        return TwoTypeMessage.of(messageF, messageT);
    }

    private ClovaResponseDto createDummyResponse() {
        return new ClovaDummyResponseDto("""
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
                """);
    }
}
