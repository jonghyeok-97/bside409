package bsise.server.reply;

import bsise.server.clovar.TwoTypeMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "두 유형의 답장 정보가 들어있는 DTO")
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyResponseDto {

    @Schema(description = "저장된 답장의 식별자")
    private final UUID replyId;

    @Schema(description = "두 유형의 답장 배열")
    private final TwoTypeMessage reply;

    @Schema(description = "답장이 저장된 시각")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @Builder
    public static ReplyResponseDto of(Reply reply) {
        return new ReplyResponseDto(reply.getId(), TwoTypeMessage.fromReply(reply), reply.getCreatedAt());
    }
}
