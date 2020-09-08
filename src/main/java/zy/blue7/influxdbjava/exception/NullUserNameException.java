package zy.blue7.influxdbjava.exception;

public class NullUserNameException extends Exception{
    public NullUserNameException() {
        super();
    }

    public NullUserNameException(String message) {
        super(message);
    }

    public NullUserNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullUserNameException(Throwable cause) {
        super(cause);
    }

    protected NullUserNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
