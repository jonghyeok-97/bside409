package site.radio.user.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WithdrawalReason {

    NOT_USED_OFTEN(1, "자주 사용하지 않아요."),
    WORRIED_ABOUT_PERSONAL_INFORMATION(2, "개인 정보가 걱정돼요."),
    INCONVENIENT_TO_USE(3, "사용하기가 불편해요."),
    INAPPROPRIATE_ANSWER(4, "답장이 마음에 들지 않아요."),
    ETC(0, "그 외 기타의 이유"),
    ;

    private final int sequence;
    private final String reason;

    @JsonCreator
    public static WithdrawalReason of(int selectedSequence) {
        return Arrays.stream(values())
                .filter(reason -> reason.sequence == selectedSequence)
                .findAny()
                .orElseThrow();
    }
}
