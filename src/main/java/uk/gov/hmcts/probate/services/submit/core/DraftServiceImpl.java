package uk.gov.hmcts.probate.services.submit.core;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftRequest;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftResponse;
import uk.gov.hmcts.probate.services.submit.services.DraftService;

@Component
public class DraftServiceImpl implements DraftService {

    @Override
    public DraftResponse saveDraft(String applicantEmail, DraftRequest draftRequest) {
        return null;
    }
}
