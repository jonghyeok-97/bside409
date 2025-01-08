package site.radio.error;

import org.springframework.security.core.AuthenticationException;

public class DormantUserLoginException extends AuthenticationException {


    public DormantUserLoginException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DormantUserLoginException(String msg) {
        super(msg);
    }
}
