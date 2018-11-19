package uk.gov.hmcts.probate.services.submit.model.v2.grantofrepresentation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LegalStatementExecutorNotApplying {

    private final String executor;
}
