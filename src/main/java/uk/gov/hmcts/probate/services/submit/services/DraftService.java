package uk.gov.hmcts.probate.services.submit.services;

import uk.gov.hmcts.probate.services.submit.model.v2.DraftRequest;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftResponse;

public interface DraftService {

    DraftResponse saveDraft(String applicantEmail, DraftRequest draftRequest);
}
