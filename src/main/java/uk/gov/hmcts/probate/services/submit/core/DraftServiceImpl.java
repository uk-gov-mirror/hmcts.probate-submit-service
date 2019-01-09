package uk.gov.hmcts.probate.services.submit.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.clients.v2.ccd.EventId;
import uk.gov.hmcts.probate.services.submit.services.CoreCaseDataService;
import uk.gov.hmcts.probate.services.submit.services.DraftService;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {

    private final CoreCaseDataService coreCaseDataService;

    private final SecurityUtils securityUtils;

    @Override
    public ProbateCaseDetails saveDraft(String applicantEmail, ProbateCaseDetails caseRequest) {
        log.info("saveDraft - Saving draft for case type: {}", caseRequest.getCaseData().getClass().getSimpleName());
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
            log.info("Found case with case Id: {}", caseResponse.getCaseInfo().getCaseId());
            return coreCaseDataService.updateCase(caseResponse.getCaseInfo().getCaseId(), caseData,
                    EventId.UPDATE_DRAFT, securityDTO);
        }
        log.info("No case found");
        return coreCaseDataService.createCase(caseData, EventId.CREATE_DRAFT, securityDTO);
    }
}
