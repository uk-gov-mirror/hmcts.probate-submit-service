package uk.gov.hmcts.probate.services.submit.model.v2;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.Payment;

@Data
@Builder
public class PaymentUpdateRequest {

    private CaseType type;

    private Payment payment;
}
