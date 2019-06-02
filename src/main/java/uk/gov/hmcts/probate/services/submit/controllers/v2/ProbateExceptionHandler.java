package uk.gov.hmcts.probate.services.submit.controllers.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.MethodArgumentNotValidExceptionDecorator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;
import uk.gov.hmcts.reform.probate.model.client.AssertFieldException;
import uk.gov.hmcts.reform.probate.model.client.ErrorResponse;
import uk.gov.hmcts.reform.probate.model.client.ValidationError;
import uk.gov.hmcts.reform.probate.model.client.ValidationErrorResponse;

import java.util.List;

@Slf4j
@ControllerAdvice
public class ProbateExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiClientException.class)
    public ResponseEntity<ErrorResponse> handleApiClientException(final ApiClientException exception){
        HttpStatus status = HttpStatus.resolve(exception.getStatus());

        if (status == null) {
            log.debug("CcdClient responded with unprocessable HttpStatus");
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(exception.getErrorResponse(), status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        MethodArgumentNotValidExceptionDecorator exceptionDecorator =
                new MethodArgumentNotValidExceptionDecorator(ex);

        List<ValidationError> errors = exceptionDecorator.getAllErrors();

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(errors);

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(AssertFieldException.class)
    public ResponseEntity<ErrorResponse> handleAssertFieldException(final AssertFieldException exception){
        return new ResponseEntity<>(exception.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
