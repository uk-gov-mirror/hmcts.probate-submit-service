package uk.gov.hmcts.probate.services.submit.core.proccessors.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.core.EventFactory;
import uk.gov.hmcts.probate.services.submit.core.SearchFieldFactory;
import uk.gov.hmcts.probate.services.submit.core.proccessors.AbstractSubmissionsProcessor;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseStatePreconditionException;
import uk.gov.hmcts.probate.services.submit.services.CoreCaseDataService;
import uk.gov.hmcts.probate.services.submit.validation.CaseDataValidatorFactory;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseEvents;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.EventId;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class UpdateCaseToDraftSubmissionsProcessor extends AbstractSubmissionsProcessor {

    private final CoreCaseDataService coreCaseDataService;
    private final EventFactory eventFactory;
    private final Map<CaseType, CaseState> createdStateMap;

    @Autowired
    public UpdateCaseToDraftSubmissionsProcessor(CoreCaseDataService coreCaseDataService, EventFactory eventFactory,
                                                 SecurityUtils securityUtils, SearchFieldFactory searchFieldFactory,
                                                 CaseDataValidatorFactory caseDataValidatorFactory,
                                                 Map<CaseType, CaseState> createdStateMap) {
        super(securityUtils, searchFieldFactory, caseDataValidatorFactory, coreCaseDataService);
        this.coreCaseDataService = coreCaseDataService;
        this.eventFactory = eventFactory;
        this.createdStateMap = createdStateMap;
    }

    @Override
    protected ProbateCaseDetails processCase(String identifier, CaseData caseData) {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        CaseType caseType = CaseType.getCaseType(caseData);
        ProbateCaseDetails caseResponse = findCase(identifier, caseType, securityDTO);
        log.info("Found case with case Id: {}", caseResponse.getCaseInfo().getCaseId());
        CaseState state = CaseState.getState(caseResponse.getCaseInfo().getState());
        CaseEvents caseEvents = eventFactory.getCaseEvents(caseType);
        if (isCreateState(state, caseType)) {
            return coreCaseDataService.updateCase(caseResponse.getCaseInfo().getCaseId(), caseData, caseEvents.getUpdateCaseApplicationEventId(), securityDTO);
        }
        checkStatePrecondition(state, caseEvents.getCreateCaseApplicationEventId());
        String caseId = caseResponse.getCaseInfo().getCaseId();
        return coreCaseDataService.updateCase(caseId, caseData, caseEvents.getCreateCaseApplicationEventId(), securityDTO);
    }

    private void checkStatePrecondition(CaseState caseState, EventId eventId) {
        if (!caseState.equals(CaseState.DRAFT)) {
            throw new CaseStatePreconditionException(caseState, eventId);
        }
    }

    private boolean isCreateState(CaseState caseState, CaseType caseType){
        CaseState createdState = Optional.ofNullable(createdStateMap.get(caseType)).orElseThrow(IllegalArgumentException::new);
        return createdState.equals(caseState);
    }
}
