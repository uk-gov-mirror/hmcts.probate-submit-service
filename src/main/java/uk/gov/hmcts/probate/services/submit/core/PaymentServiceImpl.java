package uk.gov.hmcts.probate.services.submit.core;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseNotFoundException;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseStatePreconditionException;
import uk.gov.hmcts.probate.services.submit.services.CoreCaseDataService;
import uk.gov.hmcts.probate.services.submit.services.PaymentsService;
import uk.gov.hmcts.reform.probate.model.PaymentStatus;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseEvents;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.EventId;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.ProbatePaymentDetails;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static uk.gov.hmcts.reform.probate.model.PaymentStatus.FAILED;
import static uk.gov.hmcts.reform.probate.model.PaymentStatus.INITIATED;
import static uk.gov.hmcts.reform.probate.model.PaymentStatus.SUCCESS;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.CASE_PAYMENT_FAILED;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.PA_APP_CREATED;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentsService {

    private static final Map<Pair<CaseState, PaymentStatus>, Function<CaseEvents, EventId>> PAYMENT_EVENT_MAP =
        ImmutableMap.<Pair<CaseState, PaymentStatus>, Function<CaseEvents, EventId>>builder()
            .put(Pair.of(PA_APP_CREATED, SUCCESS), CaseEvents::getCreateCaseEventId)
            .put(Pair.of(PA_APP_CREATED, FAILED), CaseEvents::getPaymentFailedEventId)
            .put(Pair.of(PA_APP_CREATED, INITIATED), CaseEvents::getPaymentFailedEventId)
            .put(Pair.of(CASE_PAYMENT_FAILED, SUCCESS), CaseEvents::getPaymentFailedToSuccessEventId)
            .put(Pair.of(CASE_PAYMENT_FAILED, FAILED), CaseEvents::getPaymentFailedAgainEventId)
            .put(Pair.of(CASE_PAYMENT_FAILED, INITIATED), CaseEvents::getPaymentFailedAgainEventId)
            .build();

    private final CoreCaseDataService coreCaseDataService;

    private final SecurityUtils securityUtils;

    private final EventFactory eventFactory;

    @Override
    public ProbateCaseDetails addPaymentToCase(String searchField, ProbatePaymentDetails paymentUpdateRequest) {
        log.info("Updating payment details for case type: {}", paymentUpdateRequest.getCaseType().getName());
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        CaseType caseType = paymentUpdateRequest.getCaseType();
        ProbateCaseDetails caseResponse = findCase(searchField, caseType, securityDTO);
        log.info("Found case with case Id: {}", caseResponse.getCaseInfo().getCaseId());
        String caseId = caseResponse.getCaseInfo().getCaseId();
        return updateCase(caseId, paymentUpdateRequest, securityDTO, caseType, caseResponse, false);
    }

    @Override
    public ProbateCaseDetails createCase(String searchField, ProbateCaseDetails probateCaseDetails) {
        CaseType caseType = CaseType.getCaseType(probateCaseDetails.getCaseData());
        log.info("Updating payment details for case type: {}", CaseType.getCaseType(probateCaseDetails.getCaseData()));
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        ProbateCaseDetails caseResponse = findCase(searchField, caseType, securityDTO);
        log.info("Found case with case Id: {}", caseResponse.getCaseInfo().getCaseId());
        String caseId = caseResponse.getCaseInfo().getCaseId();
        return updateCase(caseId, securityDTO, caseType, probateCaseDetails);
    }

    @Override
    public ProbateCaseDetails updatePaymentByCaseId(String caseId, ProbatePaymentDetails paymentUpdateRequest) {
        log.info("Updating payment details for case with id: {}", caseId);
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        ProbateCaseDetails caseResponse = findCaseById(caseId, securityDTO);
        CaseType caseType = CaseType.getCaseType(caseResponse.getCaseData());
        log.info("Found case with case Id: {}", caseResponse.getCaseInfo().getCaseId());
        return updateCase(caseId, paymentUpdateRequest, securityDTO, caseType, caseResponse, true);
    }


    private ProbateCaseDetails updateCase(String caseId, SecurityDTO securityDTO, CaseType caseType,
                                          ProbateCaseDetails probateCaseDetails) {
        CasePayment payment = probateCaseDetails.getCaseData().getPayments().get(0).getValue();
        CaseState caseState = CaseState.getState(probateCaseDetails.getCaseInfo().getState());
        CaseEvents caseEvents = eventFactory.getCaseEvents(caseType);
        EventId eventId = getEventId(caseState, payment).apply(caseEvents);
        return coreCaseDataService.updateCase(caseId, probateCaseDetails.getCaseData(), eventId, securityDTO);
    }

    private ProbateCaseDetails updateCase(String caseId, ProbatePaymentDetails paymentUpdateRequest,
                                          SecurityDTO securityDTO, CaseType caseType, ProbateCaseDetails caseResponse, Boolean asCaseWorker) {
        CaseState caseState = CaseState.getState(caseResponse.getCaseInfo().getState());
        if (CaseState.CASE_CREATED.equals(caseState)) {
            return caseResponse;
        }
        CasePayment payment = paymentUpdateRequest.getPayment();
        CaseEvents caseEvents = eventFactory.getCaseEvents(caseType);
        EventId eventId = getEventId(caseState, payment).apply(caseEvents);
        CaseData caseData = createCaseData(caseResponse, payment);

        if (asCaseWorker) {
            return coreCaseDataService.updateCaseAsCaseworker(caseId, caseData, eventId, securityDTO);
        }
        return coreCaseDataService.updateCase(caseId, caseData, eventId, securityDTO);
    }

    private ProbateCaseDetails findCase(String applicantEmail, CaseType caseType, SecurityDTO securityDTO) {
        Optional<ProbateCaseDetails> caseResponseOptional = coreCaseDataService.
            findCase(applicantEmail, caseType, securityDTO);
        return caseResponseOptional.orElseThrow(CaseNotFoundException::new);
    }

    private ProbateCaseDetails findCaseById(String caseId, SecurityDTO securityDTO) {
        Optional<ProbateCaseDetails> caseResponseOptional = coreCaseDataService.
            findCaseById(caseId, securityDTO);
        return caseResponseOptional.orElseThrow(CaseNotFoundException::new);
    }

    private Function<CaseEvents, EventId> getEventId(CaseState caseState, CasePayment payment) {
        Optional<Function<CaseEvents, EventId>> optionalFunction =
            Optional.ofNullable(PAYMENT_EVENT_MAP.get(Pair.of(caseState, payment.getStatus())));
        return optionalFunction
            .orElseThrow(() -> new CaseStatePreconditionException(caseState, payment.getStatus()));
    }

    private CaseData createCaseData(ProbateCaseDetails caseResponse, CasePayment payment) {
        CaseData caseData = caseResponse.getCaseData();
        CollectionMember collectionMember = new CollectionMember();
        collectionMember.setValue(payment);
        caseData.setPayments(Arrays.asList(collectionMember));
        return caseData;
    }
}
