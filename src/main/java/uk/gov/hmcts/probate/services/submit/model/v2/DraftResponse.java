package uk.gov.hmcts.probate.services.submit.model.v2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DraftResponse {

    private CaseData caseData;

    private CaseInfo caseInfo;
}
