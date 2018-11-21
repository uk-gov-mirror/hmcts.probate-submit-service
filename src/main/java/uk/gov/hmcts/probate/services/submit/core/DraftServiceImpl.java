package uk.gov.hmcts.probate.services.submit.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftRequest;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftResponse;
import uk.gov.hmcts.probate.services.submit.services.v2.CoreCaseDataService;
import uk.gov.hmcts.probate.services.submit.services.v2.DraftService;

@Component
public class DraftServiceImpl implements DraftService {

    private final CoreCaseDataService coreCaseDataService;

    @Autowired
    public DraftServiceImpl(CoreCaseDataService coreCaseDataService) {
        this.coreCaseDataService = coreCaseDataService;
    }

    @Override
    public DraftResponse saveDraft(String applicantEmail, DraftRequest draftRequest) {
        return coreCaseDataService.saveDraft(applicantEmail, draftRequest.getCaseData());
    }
}
