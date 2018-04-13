package uk.gov.hmcts.probate.services.submit.model;

public class SubmitException extends RuntimeException {

    private final String message;

    private final Throwable cause;

    public SubmitException(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
