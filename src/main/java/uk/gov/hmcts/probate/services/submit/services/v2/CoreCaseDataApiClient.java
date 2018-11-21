package uk.gov.hmcts.probate.services.submit.services.v2;

import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseData;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseInfo;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseType;

import java.util.Optional;

public interface CoreCaseDataApiClient {

    Optional<CaseInfo> findCase(String applicantEmail, CaseType caseType, SecurityDTO securityDTO);

    CaseInfo updateDraft(String caseId, CaseData caseData, SecurityDTO securityDTO);

    CaseInfo createDraft(CaseData caseData, SecurityDTO securityDTO);
}
