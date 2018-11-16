package uk.gov.hmcts.probate.services.submit.model.v2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutorApplying {

    private final String applyingExecutorName;
    private final String applyingExecutorPhoneNumber;
    private final String applyingExecutorEmail;
    private final Address applyingExecutorAddress;
    private String applyingExecutorOtherNames;
    private String applyingExecutorOtherNamesReason;
    private String applyingExecutorOtherReason;
}
