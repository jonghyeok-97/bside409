package site.admin.retrieve.letter;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LetterRetrieveResult {

    private final UUID replyId;

    private final UUID letterId;

    private final String username;

    private final String content;

    private final String preference;

    private final TwoTypeMessage reply;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    public static LetterRetrieveResult of(Reply reply) {
        return LetterRetrieveResult.builder()
                .replyId(reply.getId())
                .letterId(reply.getLetter().getId())
                .username(reply.getLetter().getUser().getNickname())
                .content(reply.getLetter().getMessage())
                .preference(reply.getLetter().getPreference())
                .reply(TwoTypeMessage.fromReply(reply))
                .createdAt(reply.getCreatedAt())
                .build();
    }
}
