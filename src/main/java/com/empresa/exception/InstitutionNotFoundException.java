package com.empresa.exception;

public class InstitutionNotFoundException extends RuntimeException {
    
    public InstitutionNotFoundException(String message) {
        super(message);
    }
    
    public InstitutionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}