package site.radio.error;

public class DuplicateDailyReportException extends IllegalArgumentException {

    public DuplicateDailyReportException(String message) {
        super(message);
    }
}
