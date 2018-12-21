package uk.gov.hmcts.probate.services.submit.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.clients.v2.ccd.EventId;
import uk.gov.hmcts.probate.services.submit.controllers.v2.CoreCaseDataService;
import uk.gov.hmcts.probate.services.submit.controllers.v2.DraftService;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {

    private final CoreCaseDataService coreCaseDataService;

    private final SecurityUtils securityUtils;

    @Override
    public ProbateCaseDetails saveDraft(String applicantEmail, ProbateCaseDetails caseRequest) {
        CaseData caseData = caseRequest.getCaseData();
        Assert.isTrue(caseData.getPrimaryApplicantEmailAddress().equals(applicantEmail),
                "Applicant email on path must match case data");
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        CaseType caseType = CaseType.getCaseType(caseData);
        Optional<ProbateCaseDetails> caseInfoOptional = coreCaseDataService.findCase(applicantEmail, caseType, securityDTO);
        return saveDraft(securityDTO, caseData, caseInfoOptional);
    }

    private ProbateCaseDetails saveDraft(SecurityDTO securityDTO, CaseData caseData,
                                   Optional<ProbateCaseDetails> caseResponseOptional) {
        if (caseResponseOptional.isPresent()) {
            ProbateCaseDetails caseResponse = caseResponseOptional.get();
            return coreCaseDataService.updateCase(caseResponse.getCaseInfo().getCaseId(), caseData,
                    EventId.UPDATE_DRAFT, securityDTO);
        }
        return coreCaseDataService.createCase(caseData, EventId.CREATE_DRAFT, securityDTO);
    }
}
