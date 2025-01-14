package site.radio.clova.dailyReport;

import java.util.List;
import java.util.stream.Collectors;
import site.radio.letter.Letter;

public class DailyReportPreprocessor {

    private static final String LETTER_SEPARATOR = "sharpie-sep";

    public static String requestClovaAnalysis(List<Letter> letters) {
        // 편지 내용 구분자 동적 생성
        String msgSeparator = Long.toHexString(Double.doubleToLongBits(Math.random()));

        return letters.stream()
                .map(letter -> String.format("<%s:%s>\n%s\n</%s:%s>",
                        LETTER_SEPARATOR, msgSeparator,
                        reformatMsg(letter.getMessage()),
                        LETTER_SEPARATOR, msgSeparator))
                .collect(Collectors.joining("\n"));
    }

    private static String reformatMsg(String input) {
        return input
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("&", "&amp;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&apos;");
    }
}
