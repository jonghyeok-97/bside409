package bsise.server.dummy;

import bsise.server.clovar.ClovaMessageFormat;
import bsise.server.clovar.ClovaResponseDto;
import lombok.Getter;

@Getter
public class ClovaDummyResponseDto extends ClovaResponseDto {

    private StatusDummyDto status;
    private ResultDummyDto result;

    public ClovaDummyResponseDto(String message) {
        status = new StatusDummyDto();
        result = new ResultDummyDto(message);
    }

    @Getter
    public static class StatusDummyDto extends StatusDto {

        private String code;

        public StatusDummyDto() {
            code = "200";
        }
    }

    @Getter
    public static class ResultDummyDto extends ResultDto {

        private final ClovaMessageFormat message;

        public ResultDummyDto(String message) {
            this.message = new ClovaMessageDummyFormat(message);
        }
    }

    public static class ClovaMessageDummyFormat extends ClovaMessageFormat {

        private final String content;

        public ClovaMessageDummyFormat(String content) {
            this.content = content;
        }

        @Override
        public String getContent() {
            return content;
        }
    }

    public boolean hasErrorCode() {
        return status.code.startsWith("4") || status.code.startsWith("5");
    }

    public String getResultMessage() {
        return result.message.getContent();
    }
}
