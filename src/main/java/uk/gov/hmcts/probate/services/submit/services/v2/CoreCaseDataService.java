package uk.gov.hmcts.probate.services.submit.services.v2;

import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.services.submit.clients.v2.ccd.EventId;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

import java.util.Optional;

public interface CoreCaseDataService {

    Optional<CaseResponse> findCase(String applicantEmail, CaseType caseType, SecurityDTO securityDTO) ;

    CaseResponse updateCase(String caseId, CaseData caseData, EventId eventId, SecurityDTO securityDTO);

    CaseResponse createCase(CaseData caseData, EventId eventId, SecurityDTO securityDTO);

}
