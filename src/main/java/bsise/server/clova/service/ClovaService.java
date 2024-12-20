package bsise.server.clova.service;

import static bsise.server.user.domain.Preference.F;
import static bsise.server.user.domain.Preference.T;

import bsise.server.clova.client.ClovaFeignClient;
import bsise.server.clova.dailyReport.ClovaDailyReportRequestDto;
import bsise.server.clova.dto.ClovaRequestDto;
import bsise.server.clova.dto.ClovaResponseDto;
import bsise.server.clova.letter.ClovaLetterReplyRequestDto;
import bsise.server.clova.letter.MessageExtractor;
import bsise.server.clova.letter.TwoTypeMessage;
import bsise.server.clova.weekly.ClovaWeeklyReportRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("prod")
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(ClovaKeyProperties.class)
public class ClovaService {

    private final ClovaKeyProperties properties;
    private final ClovaFeignClient client;

    public ClovaResponseDto send(String message) {
        return sendRequestToClova(ClovaLetterReplyRequestDto.from(message));
    }

    public ClovaResponseDto sendDailyReportRequest(String message) {
        return sendRequestToClova(ClovaDailyReportRequestDto.from(message));
    }

    public ClovaResponseDto sendWeeklyReportRequest(ClovaWeeklyReportRequestDto dto) {
        return sendRequestToClova(dto);
    }

    private ClovaResponseDto sendRequestToClova(ClovaRequestDto clovaRequestDto) {
        return client.sendToClova(
                properties.getApiKey(),
                properties.getApigwKey(),
                properties.getRequestId(),
                clovaRequestDto);
    }

    public TwoTypeMessage extract(ClovaResponseDto response) {
        String messageF = MessageExtractor.extract(response.getResultMessage(), F);
        String messageT = MessageExtractor.extract(response.getResultMessage(), T);

        return TwoTypeMessage.of(messageF, messageT);
    }
}
