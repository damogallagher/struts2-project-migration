package com.empresa.exception;

public class DuplicateInstitutionException extends RuntimeException {
    
    public DuplicateInstitutionException(String message) {
        super(message);
    }
    
    public DuplicateInstitutionException(String message, Throwable cause) {
        super(message, cause);
    }
}