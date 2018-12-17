package uk.gov.hmcts.probate.services.submit.services.v2;

import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

public interface DraftService {

    ProbateCaseDetails saveDraft(String applicantEmail, ProbateCaseDetails caseRequest);

}
