package com.ktube.vod.identification;

public class IdentificationFailureException extends RuntimeException {

    public IdentificationFailureException() {
        super();
    }

    public IdentificationFailureException(String message) {
        super(message);
    }
}
