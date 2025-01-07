package bsise.server.error;

public class WeeklyReportAlreadyExistsException extends EntityAlreadyExistsException {

    public WeeklyReportAlreadyExistsException(String message) {
        super(message);
    }
}
