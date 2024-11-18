package bsise.server.error;

import jakarta.persistence.EntityNotFoundException;

public class DailyReportNotFoundException extends EntityNotFoundException {

    public DailyReportNotFoundException(String message) {
        super(message);
    }
}
