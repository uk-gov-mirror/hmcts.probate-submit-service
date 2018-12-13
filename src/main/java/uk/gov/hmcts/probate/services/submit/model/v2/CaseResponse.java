package uk.gov.hmcts.probate.services.submit.model.v2;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseInfo;

@Data
@Builder
public class CaseResponse {

    private CaseData caseData;

    private CaseInfo caseInfo;
}
