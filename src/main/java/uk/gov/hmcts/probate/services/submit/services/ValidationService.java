package uk.gov.hmcts.probate.services.submit.services;

import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

public interface ValidationService {

    void validate(ProbateCaseDetails probateCaseDetails);

    void validateForSubmission(ProbateCaseDetails probateCaseDetails);

}
