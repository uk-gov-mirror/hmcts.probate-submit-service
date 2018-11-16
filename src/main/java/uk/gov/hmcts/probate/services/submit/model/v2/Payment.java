package uk.gov.hmcts.probate.services.submit.model.v2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payment {

    private final String status;

    private final String date;

    private final String reference;

    private final String amount;

    private final String method;

    private final String transactionId;

    private final String siteId;
}
