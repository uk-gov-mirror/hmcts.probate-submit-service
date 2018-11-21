package uk.gov.hmcts.probate.services.submit.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseData;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseInfo;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseType;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftRequest;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftResponse;
import uk.gov.hmcts.probate.services.submit.services.v2.CoreCaseDataApiClient;
import uk.gov.hmcts.probate.services.submit.services.v2.DraftService;

import java.util.Optional;

@Component
public class DraftServiceImpl implements DraftService {

    private final CoreCaseDataApiClient coreCaseDataApiClient;

    private final SecurityUtils securityUtils;

    @Autowired
    public DraftServiceImpl(CoreCaseDataApiClient coreCaseDataApiClient, SecurityUtils securityUtils) {
        this.coreCaseDataApiClient = coreCaseDataApiClient;
        this.securityUtils = securityUtils;
    }

    @Override
    public DraftResponse saveDraft(String applicantEmail, DraftRequest draftRequest) {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        CaseData caseData = draftRequest.getCaseData();
        CaseType caseType = CaseType.getCaseType(caseData);
        Optional<CaseInfo> caseInfoOptional = coreCaseDataApiClient.findCase(applicantEmail, caseType, securityDTO);

        CaseInfo caseInfo = saveDraft(securityDTO, caseData, caseInfoOptional);
        return DraftResponse.builder()
                .caseData(caseData)
                .caseInfo(caseInfo)
                .build();
    }

    private CaseInfo saveDraft(SecurityDTO securityDTO, CaseData caseData, Optional<CaseInfo> caseInfoOptional) {
        if (caseInfoOptional.isPresent()) {
            CaseInfo caseInfo = caseInfoOptional.get();
            return coreCaseDataApiClient.updateDraft(caseInfo.getCaseId(), caseData, securityDTO);
        }
        return coreCaseDataApiClient.createDraft(caseData, securityDTO);
    }
}
