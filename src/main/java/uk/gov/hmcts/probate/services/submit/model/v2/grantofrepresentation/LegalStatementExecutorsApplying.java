package uk.gov.hmcts.probate.services.submit.model.v2.grantofrepresentation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LegalStatementExecutorsApplying {

    private final LegalStatementExecutorApplying value;

    private final String id;

}
