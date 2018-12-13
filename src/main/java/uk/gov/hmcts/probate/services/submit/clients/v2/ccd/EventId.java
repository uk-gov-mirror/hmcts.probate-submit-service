package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static uk.gov.hmcts.probate.services.submit.clients.v2.ccd.CaseState.CASE_PAYMENT_FAILED;
import static uk.gov.hmcts.probate.services.submit.clients.v2.ccd.CaseState.DRAFT;
import static uk.gov.hmcts.probate.services.submit.clients.v2.ccd.CaseState.PA_APP_CREATED;

@RequiredArgsConstructor
public enum EventId {

    CREATE_DRAFT("createDraft", null),
    UPDATE_DRAFT("updateDraft", DRAFT),
    CREATE_APPLICATION("createApplication", DRAFT),
    PAYMENT_SUCCESS("paymentSuccessCase", PA_APP_CREATED),
    PAYMENT_FAILED("createCasePaymentFailed", CASE_PAYMENT_FAILED),
    PAYMENT_FAILED_TO_SUCCESS("createCasePaymentSuccess", CASE_PAYMENT_FAILED),
    PAYMENT_FAILED_AGAIN("createCasePaymentFailedMultiple", CASE_PAYMENT_FAILED);

    @Getter
    private final String name;

    @Getter
    private final CaseState preconditionState;

}
