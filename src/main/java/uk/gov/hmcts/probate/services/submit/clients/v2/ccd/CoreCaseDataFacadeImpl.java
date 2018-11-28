package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseData;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseInfo;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseType;
import uk.gov.hmcts.probate.services.submit.services.v2.CoreCaseDataFacade;

import java.util.Optional;

@Component
public class CoreCaseDataFacadeImpl implements CoreCaseDataFacade {

    private final CcdClientApi ccdClientApi;

    @Autowired
    public CoreCaseDataFacadeImpl(CcdClientApi ccdClientApi) {
        this.ccdClientApi = ccdClientApi;
    }

    @Override
    public Optional<CaseInfo> findCase(String applicantEmail, CaseType caseType, SecurityDTO securityDTO) {
        return ccdClientApi.findCase(applicantEmail, caseType, securityDTO);
    }

    @Override
    public CaseInfo updateDraft(String caseId, CaseData caseData, SecurityDTO securityDTO) {
        return ccdClientApi.updateCase(caseId, caseData, EventId.UPDATE_DRAFT, securityDTO);
    }

    @Override
    public CaseInfo createDraft(CaseData caseData, SecurityDTO securityDTO) {
        return ccdClientApi.createCase(caseData, EventId.CREATE_DRAFT, securityDTO);
    }
}
