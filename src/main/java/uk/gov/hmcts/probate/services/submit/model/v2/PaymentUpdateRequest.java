package uk.gov.hmcts.probate.services.submit.model.v2;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

@Data
@Builder
public class PaymentUpdateRequest {

    private CaseType type;

    private CasePayment payment;
}
