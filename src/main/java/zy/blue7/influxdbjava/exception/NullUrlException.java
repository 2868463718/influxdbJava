package zy.blue7.influxdbjava.exception;

public class NullUrlException extends Exception{
    public NullUrlException() {
        super();
    }

    public NullUrlException(String message) {
        super(message);
    }

    public NullUrlException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullUrlException(Throwable cause) {
        super(cause);
    }

    protected NullUrlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
