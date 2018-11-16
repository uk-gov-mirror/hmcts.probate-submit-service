package uk.gov.hmcts.probate.services.submit.model.v2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutorNotApplying {

    private final String notApplyingExecutorName;
    private final String notApplyingExecutorNameOnWill;
    private final String notApplyingExecutorNameDifferenceComment;
    private final String notApplyingExecutorReason;
    private final String notApplyingExecutorNotified;
}
