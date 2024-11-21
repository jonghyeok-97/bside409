package bsise.server.error;

import jakarta.persistence.EntityNotFoundException;

public class DuplicationWeeklyReportException extends EntityNotFoundException {

    public DuplicationWeeklyReportException(String message) {
        super(message);
    }
}
