package bsise.server.clovar;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Clova Studio 로부터 응답을 받는 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClovaResponseDto {

    private static final String CLIENT_ERROR = "4";
    private static final String CLOVA_ERROR = "5";

    private StatusDto status;
    private ResultDto result;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class StatusDto {

        private String code;
        private String message;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResultDto {

        private ClovaMessageFormat message;
        private int inputLength;
        private int outputLength;
        private String stopReason;
        private long seed;
        private List<AiFilterDto> aiFilter;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AiFilterDto {

        private String groupName;
        private String name;
        private String score;
        private String result;
    }

    public boolean hasErrorCode() {
        return status.code.startsWith(CLIENT_ERROR) || status.code.startsWith(CLOVA_ERROR);
    }

    public String getResultMessage() {
        return result.message.getContent();
    }
}
