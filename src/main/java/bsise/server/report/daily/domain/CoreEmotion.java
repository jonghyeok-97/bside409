package bsise.server.report.daily.domain;

import java.util.Arrays;

public enum CoreEmotion {
    기쁨, 슬픔, 분노, 두려움, 놀라움, 혐오, 열망, 수용, 중립;

    public static CoreEmotion findOrNeutral(String emotion) {
        return Arrays.stream(values())
                .filter(e -> e.name().equals(emotion))
                .findAny()
                .orElse(중립);
    }
}
