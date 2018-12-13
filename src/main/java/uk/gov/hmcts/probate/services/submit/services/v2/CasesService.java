package uk.gov.hmcts.probate.services.submit.services.v2;

import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

public interface CasesService {

    CaseResponse getCase(String applicantEmail, CaseType caseType);
}
