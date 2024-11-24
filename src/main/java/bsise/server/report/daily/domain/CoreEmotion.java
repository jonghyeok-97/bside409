package bsise.server.report.daily.domain;

import java.util.Arrays;

public enum CoreEmotion {
    기쁨, 슬픔, 분노, 두려움, 놀라움, 혐오, 열망, 수용, 중립;

    public static boolean isAbsent(String emotion) {
        return Arrays.stream(values())
                .noneMatch(coreEmotion -> coreEmotion.name().equals(emotion));
    }
}
