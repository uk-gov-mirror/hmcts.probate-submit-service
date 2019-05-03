package uk.gov.hmcts.probate.services.submit.model.v2.exception;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import uk.gov.hmcts.reform.probate.model.client.ValidationError;

import java.util.List;
import java.util.stream.Collectors;

public class MethodArgumentNotValidExceptionDecorator extends Exception {

    private MethodArgumentNotValidException exception;

    public MethodArgumentNotValidExceptionDecorator(MethodArgumentNotValidException ex) {
        this.exception=ex;
    }

    public List<ValidationError> getAllErrors() {
        List<FieldError> errors = this.exception.getBindingResult().getFieldErrors();

        return errors.stream()
                .map(error -> new ValidationError(
                        error.getCode(),
                        error.getField(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());
    }
}
