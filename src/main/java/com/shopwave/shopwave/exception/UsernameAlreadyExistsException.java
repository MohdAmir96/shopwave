package com.shopwave.shopwave.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message  ) {
        super(message);
    }
}
