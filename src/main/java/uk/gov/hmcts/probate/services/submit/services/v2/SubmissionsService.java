package uk.gov.hmcts.probate.services.submit.services.v2;

import uk.gov.hmcts.probate.services.submit.model.v2.CaseRequest;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;

public interface SubmissionsService {

    CaseResponse submit(String applicantEmail, CaseRequest caseRequest);
}
