package com.eirs.pairs.exception;

public class InternalServerException extends RuntimeException {

    public InternalServerException() {
        super();
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(Throwable cause) {
        super(cause);
    }
}
