package uk.gov.hmcts.probate.services.submit.services.v2;

import uk.gov.hmcts.probate.services.submit.model.v2.CaseData;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftResponse;

public interface CoreCaseDataService {

    DraftResponse saveDraft(String applicantEmail, CaseData caseData);
}
