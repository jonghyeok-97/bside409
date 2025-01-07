package site.radio.error;

public class DailyReportAlreadyExistsException extends EntityAlreadyExistsException {

    public DailyReportAlreadyExistsException(String message) {
        super(message);
    }
}
