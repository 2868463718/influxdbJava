package zy.blue7.influxdbjava.exception;

public class NullDataBaseException extends Exception{
    public NullDataBaseException() {
        super();
    }

    public NullDataBaseException(String message) {
        super(message);
    }

    public NullDataBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullDataBaseException(Throwable cause) {
        super(cause);
    }

    protected NullDataBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
