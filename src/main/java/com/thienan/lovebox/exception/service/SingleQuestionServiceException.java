package com.thienan.lovebox.exception.service;

public class SingleQuestionServiceException extends RuntimeException {

    public SingleQuestionServiceException(String message) {
        super(message);
    }

    public SingleQuestionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
