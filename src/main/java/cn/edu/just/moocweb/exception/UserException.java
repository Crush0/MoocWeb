package cn.edu.just.moocweb.exception;

public class UserException extends ServiceException{
    public UserException() {
    }

    public UserException(Integer code, String message) {
        super(code, message);
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }

    public UserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
