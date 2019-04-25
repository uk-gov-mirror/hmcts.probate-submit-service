package uk.gov.hmcts.probate.services.submit.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.submit.core.proccessors.impl.CreateCaseSubmissionsProcessor;
import uk.gov.hmcts.probate.services.submit.core.proccessors.impl.UpdateCaseToDraftSubmissionsProcessor;
import uk.gov.hmcts.probate.services.submit.services.CasesService;
import uk.gov.hmcts.probate.services.submit.services.SubmissionsService;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.SubmitResult;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionsServiceImpl implements SubmissionsService {

    private final UpdateCaseToDraftSubmissionsProcessor updateCaseToDraftSubmissionProcessor;
    private final CreateCaseSubmissionsProcessor createCaseSubmissionProcessor;
    private final CasesService casesService;

    @Override
    public SubmitResult createCase(String identifier, ProbateCaseDetails caseRequest) {
        log.info("Insert for case type: {}", caseRequest.getCaseData().getClass().getSimpleName());
        return createCaseSubmissionProcessor.process(identifier, () -> caseRequest);
    }

    @Override
    public SubmitResult updateDraftToCase(String identifier, CaseType caseType) {
        log.info("Update for case type: {}", caseType.getName());
        return updateCaseToDraftSubmissionProcessor.process(identifier, () -> casesService.getCase(identifier, caseType));
    }
}
