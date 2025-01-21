package site.radio.error;

public class AsyncErrorException extends RuntimeException {

    public AsyncErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
