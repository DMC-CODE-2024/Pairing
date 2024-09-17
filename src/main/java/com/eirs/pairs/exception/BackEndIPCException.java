package com.eirs.pairs.exception;

public class BackEndIPCException extends RuntimeException {
    public BackEndIPCException() {
        super();
    }

    public BackEndIPCException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackEndIPCException(String message) {
        super(message);
    }

    public BackEndIPCException(Throwable cause) {
        super(cause);
    }
}
