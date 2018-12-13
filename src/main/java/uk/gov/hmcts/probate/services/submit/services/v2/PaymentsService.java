package uk.gov.hmcts.probate.services.submit.services.v2;

import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;
import uk.gov.hmcts.probate.services.submit.model.v2.PaymentUpdateRequest;

public interface PaymentsService {

    CaseResponse addPaymentToCase(String applicantEmail, PaymentUpdateRequest paymentUpdateRequest);
}
