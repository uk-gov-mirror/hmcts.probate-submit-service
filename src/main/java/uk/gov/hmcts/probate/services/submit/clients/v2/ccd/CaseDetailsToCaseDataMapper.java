package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CaseDetailsToCaseDataMapper {

    private final ObjectMapper objectMapper;

    public CaseData map(CaseDetails caseDetails) {
        Map<String, Object> caseData = new HashMap();
        caseData.putAll(caseDetails.getData());
        caseData.put("type", caseDetails.getCaseTypeId());

        return objectMapper.convertValue(caseData, CaseData.class);
    }

}
