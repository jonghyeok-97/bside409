package site.admin.retrieve.letter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TwoTypeMessage {

    @JsonProperty(value = "message_f")
    private final String messageForF;

    @JsonProperty(value = "message_t")
    private final String messageForT;

    public static TwoTypeMessage fromReply(Reply reply) {
        return new TwoTypeMessage(reply.getMessageForF(), reply.getMessageForT());
    }
}
