package uk.gov.hmcts.probate.services.submit.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseAlreadyExistsException;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseNotFoundException;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseStatePreconditionException;
import uk.gov.hmcts.probate.services.submit.services.CoreCaseDataService;
import uk.gov.hmcts.probate.services.submit.validation.CaseDataValidatorFactory;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseEvents;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.EventId;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

import java.util.Optional;

@Slf4j
@Component
public class CreateCaseSubmissionService extends AbstractSubmissionsService {

    private final CoreCaseDataService coreCaseDataService;
    private final EventFactory eventFactory;

    @Autowired
    public CreateCaseSubmissionService(CoreCaseDataService coreCaseDataService, EventFactory eventFactory, SecurityUtils securityUtils, SearchFieldFactory searchFieldFactory, CaseDataValidatorFactory caseDataValidatorFactory) {
        super(securityUtils,searchFieldFactory, caseDataValidatorFactory, coreCaseDataService);
        this.coreCaseDataService=coreCaseDataService;
        this.eventFactory=eventFactory;

    }

    @Override
    ProbateCaseDetails processCase(String identifier, CaseData caseData, CaseType caseType, SecurityDTO securityDTO) {
        ProbateCaseDetails caseResponse = findCase(identifier, CaseType.getCaseType(caseData), securityDTO);
        checkDoesCaseExist(identifier, CaseType.getCaseType(caseData), securityDTO);
        log.info("Case not found with case Id: {}", identifier);
        CaseEvents caseEvents = eventFactory.getCaseEvents(caseType);
        return coreCaseDataService.createCase(caseData, caseEvents.getCreateCaseApplicationEventId(), securityDTO);
    }

    private void checkDoesCaseExist(String searchField, CaseType caseType, SecurityDTO securityDTO) {
        Optional<ProbateCaseDetails> caseResponseOptional = coreCaseDataService.
                findCase(searchField, caseType, securityDTO);
        if(caseResponseOptional.isPresent()){
            throw new CaseAlreadyExistsException(searchField);
        }
    }


    private void checkStatePrecondition(CaseState caseState, EventId eventId) {
        if (!caseState.equals(CaseState.DRAFT)) {
            throw new CaseStatePreconditionException(caseState, eventId);
        }
    }
}
