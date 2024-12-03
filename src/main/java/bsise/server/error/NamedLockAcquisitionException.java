package bsise.server.error;

public class NamedLockAcquisitionException extends RuntimeException {

    public NamedLockAcquisitionException(String message) {
        super(message);
    }
}
