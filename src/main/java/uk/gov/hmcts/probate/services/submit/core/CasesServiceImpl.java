package uk.gov.hmcts.probate.services.submit.core;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseNotFoundException;
import uk.gov.hmcts.probate.services.submit.services.CasesService;
import uk.gov.hmcts.probate.services.submit.services.CoreCaseDataService;
import uk.gov.hmcts.probate.services.submit.services.ValidationService;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseEvents;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.EventId;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static uk.gov.hmcts.reform.probate.model.cases.CaseState.CASE_PAYMENT_FAILED;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.DRAFT;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.PA_APP_CREATED;

@Slf4j
@Component
@RequiredArgsConstructor
public class CasesServiceImpl implements CasesService {

    private final CoreCaseDataService coreCaseDataService;

    private final SecurityUtils securityUtils;

    private final EventFactory eventFactory;

    private final SearchFieldFactory searchFieldFactory;

    private final ValidationService validationService;

    private final Map<CaseState, Function<CaseEvents, EventId>> eventMap = ImmutableMap.<CaseState, Function<CaseEvents, EventId>>builder()
        .put(DRAFT, CaseEvents::getUpdateDraftEventId)
        .put(PA_APP_CREATED, CaseEvents::getUpdateCaseApplicationEventId)
        .put(CASE_PAYMENT_FAILED, CaseEvents::getUpdatePaymentFailedEventId)
        .build();

    @Override
    public ProbateCaseDetails getCase(String searchField, CaseType caseType) {
        log.info("Getting case of caseType: {}", caseType.getName());
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        Optional<ProbateCaseDetails> caseResponseOptional = coreCaseDataService
                .findCase(searchField, caseType, securityDTO);
        return caseResponseOptional.orElseThrow(CaseNotFoundException::new);
    }

    @Override
    public ProbateCaseDetails getCaseByApplicantEmail(String searchField, CaseType caseType) {
        log.info("Getting case of caseType: {}", caseType.getName());
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        Optional<ProbateCaseDetails> caseResponseOptional = coreCaseDataService
                .findCaseByApplicantEmail(searchField, caseType, securityDTO);
        return caseResponseOptional.orElseThrow(CaseNotFoundException::new);
    }

    @Override
    public List<ProbateCaseDetails> getAllCases(CaseType caseType) {
        log.info("Getting all cases of caseType: {}", caseType.getName());
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        return coreCaseDataService
                .findCases(caseType, securityDTO);
    }

    @Override
    public ProbateCaseDetails getCaseById(String caseId) {
        log.info("Getting case by caseId: {}", caseId);
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        Optional<ProbateCaseDetails> caseResponseOptional = coreCaseDataService
                .findCaseById(caseId, securityDTO);
        return caseResponseOptional.orElseThrow(CaseNotFoundException::new);
    }

    @Override
    public ProbateCaseDetails saveCase(String searchField, ProbateCaseDetails probateCaseDetails) {
        log.info("saveDraft - Saving draft for case type: {}", probateCaseDetails.getCaseData().getClass().getSimpleName());
        return saveCase(searchField, probateCaseDetails, Boolean.FALSE);
    }

    @Override
    public ProbateCaseDetails initiateCase(ProbateCaseDetails probateCaseDetails) {
        log.info("initiateCase - Initiating case for case type: {}", probateCaseDetails.getCaseData().getClass().getSimpleName());
        CaseData caseData = probateCaseDetails.getCaseData();
        CaseType caseType = CaseType.getCaseType(caseData);
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        CaseEvents caseEvents = eventFactory.getCaseEvents(caseType);
        return coreCaseDataService.createCase(caseData, caseEvents.getCreateDraftEventId(), securityDTO);

    }

    @Override
    public ProbateCaseDetails initiateCaseAsCaseworker(ProbateCaseDetails probateCaseDetails) {
        log.info("initiateCase as caseworker- Initiating case for case type: {}", probateCaseDetails.getCaseData().getClass().getSimpleName());
        CaseData caseData = probateCaseDetails.getCaseData();
        CaseType caseType = CaseType.getCaseType(caseData);
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        CaseEvents caseEvents = eventFactory.getCaseEvents(caseType);
        return coreCaseDataService.createCaseAsCaseworker(caseData, caseEvents.getCreateDraftEventId(), securityDTO);
    }

    @Override
    public ProbateCaseDetails saveCaseAsCaseworker(String searchField, ProbateCaseDetails probateCaseDetails) {
        log.info("saveCaseAsCaseworker - Saving draft as caseworkefor case type: {}", probateCaseDetails.getCaseData().getClass().getSimpleName());
        return saveCase(searchField, probateCaseDetails, Boolean.TRUE);
    }

    private ProbateCaseDetails saveCase(String searchField, ProbateCaseDetails probateCaseDetails, Boolean asCaseworker) {
        log.info("saveDraft - Saving draft for case type: {}", probateCaseDetails.getCaseData().getClass().getSimpleName());
        CaseData caseData = probateCaseDetails.getCaseData();
        CaseType caseType = CaseType.getCaseType(caseData);
        if(!caseType.equals(CaseType.GRANT_OF_REPRESENTATION)) {
            Pair<String, String> searchFieldValuePair = searchFieldFactory.getSearchFieldValuePair(caseType, caseData);
            String searchValue = searchFieldValuePair.getRight();
            Assert.isTrue(searchValue.equals(searchField), "Applicant email on path must match case data");
        }
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        Optional<ProbateCaseDetails> caseInfoOptional = coreCaseDataService.findCase(searchField, caseType, securityDTO);
        return saveCase(securityDTO, caseType, caseData, caseInfoOptional, asCaseworker);

    }


    private ProbateCaseDetails saveCase(SecurityDTO securityDTO, CaseType caseType, CaseData caseData,
                                        Optional<ProbateCaseDetails> caseResponseOptional, Boolean asCaseworker) {
        CaseEvents caseEvents = eventFactory.getCaseEvents(caseType);
        if (caseResponseOptional.isPresent()) {
            ProbateCaseDetails caseResponse = caseResponseOptional.get();
            CaseState state = caseResponse.getCaseInfo().getState();
            log.info("Found case with case Id: {}", caseResponse.getCaseInfo().getCaseId());
            EventId eventId = eventMap.get(state).apply(caseEvents);
            if(asCaseworker) {
                return coreCaseDataService.updateCaseAsCaseworker(caseResponse.getCaseInfo().getCaseId(), caseData, eventId, securityDTO);
            }
            else{
                return coreCaseDataService.updateCase(caseResponse.getCaseInfo().getCaseId(), caseData, eventId, securityDTO);
            }
        }
        log.info("No case found");
        return coreCaseDataService.createCase(caseData, caseEvents.getCreateDraftEventId(), securityDTO);
    }


    @Override
    public ProbateCaseDetails getCaseByInvitationId(String invitationId, CaseType caseType) {
        log.info("Getting case of caseType: {}", caseType.getName());
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        Optional<ProbateCaseDetails> caseResponseOptional = coreCaseDataService
            .findCaseByInviteId(invitationId, caseType, securityDTO);
        return caseResponseOptional.orElseThrow(CaseNotFoundException::new);
    }


    @Override
    public ProbateCaseDetails validate(String searchField, CaseType caseType) {
        log.info("Validating case of caseType: {}", caseType.getName());
        ProbateCaseDetails probateCaseDetails = getCase(searchField, caseType);
        validationService.validate(probateCaseDetails);
        return probateCaseDetails;
    }

    @Override
    public void grantAccessForCase(CaseType caseType, String caseId, String userId) {
        log.info("Grant access for case of caseType: {}", caseType.getName());
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        coreCaseDataService.grantAccessForCase(caseType, caseId, userId, securityDTO);

    }
}
