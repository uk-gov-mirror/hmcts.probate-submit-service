package uk.gov.hmcts.probate.services.submit.controllers.v2;

import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.ProbatePaymentDetails;

public interface PaymentsService {

    ProbateCaseDetails addPaymentToCase(String applicantEmail, ProbatePaymentDetails probatePaymentDetails);
}
