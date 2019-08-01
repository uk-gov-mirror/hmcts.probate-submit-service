package uk.gov.hmcts.probate.services.submit.model.v2.exception;

import lombok.Getter;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class CaseValidationException extends RuntimeException {

    @Getter
    private transient Set<ConstraintViolation<CaseData>> constraintViolations;

    public CaseValidationException(Set<ConstraintViolation<CaseData>> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }
}
