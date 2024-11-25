package bsise.server.error;

import jakarta.persistence.EntityNotFoundException;

public class WeeklyReportNotFoundException extends EntityNotFoundException {

    public WeeklyReportNotFoundException(String message) {
        super(message);
    }
}
