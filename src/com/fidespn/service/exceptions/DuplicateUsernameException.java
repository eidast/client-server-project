package com.fidespn.service.exceptions;

public class DuplicateUsernameException extends Exception {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}