package org.hrsys.exception;

public class NotAuthoriseException extends RuntimeException{
    public NotAuthoriseException(String message) {
        super(message);
    }
}
