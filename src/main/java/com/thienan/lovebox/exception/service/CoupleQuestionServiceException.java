package com.thienan.lovebox.exception.service;

public class CoupleQuestionServiceException extends RuntimeException {

    public CoupleQuestionServiceException(String message) {
        super(message);
    }

    public CoupleQuestionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
