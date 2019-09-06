package uk.gov.hmcts.probate.services.submit.services;

import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

public interface CasesService {

    ProbateCaseDetails getCase(String searchField, CaseType caseType);

    ProbateCaseDetails getCaseById(String caseId);

    ProbateCaseDetails getCaseByInvitationId(String invitationId, CaseType caseType);

    ProbateCaseDetails saveCase(String searchField, ProbateCaseDetails probateCaseDetails);

    ProbateCaseDetails validate(String searchField, CaseType caseType);

    ProbateCaseDetails saveCaseAsCaseworker(String toLowerCase, ProbateCaseDetails caseRequest);
}
