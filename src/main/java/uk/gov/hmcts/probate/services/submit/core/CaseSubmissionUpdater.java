package uk.gov.hmcts.probate.services.submit.core;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.probate.model.PaymentStatus;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

import java.util.Map;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class CaseSubmissionUpdater {

    private final RegistryService registryService;

    private final Map<CaseType, Consumer<CaseData>> caseTypeUpdaterMap = ImmutableMap.<CaseType, Consumer<CaseData>>builder()
        .put(CaseType.GRANT_OF_REPRESENTATION, this::updateGrantOfRepresentation)
        .put(CaseType.CAVEAT, this::updateCaveat)
        .build();

    public void updateCaseForSubmission(CaseData caseData, PaymentStatus paymentStatus) {
        CaseType caseType = CaseType.getCaseType(caseData);
        Consumer<CaseData> caseDataConsumer = caseTypeUpdaterMap.get(caseType);
        if (paymentStatus.equals(PaymentStatus.SUCCESS) && caseDataConsumer != null) {
            caseDataConsumer.accept(caseData);
        }
    }

    private void updateGrantOfRepresentation(CaseData caseData) {
        registryService.updateRegistry(caseData);
    }

    private void updateCaveat(CaseData caseData) {
        registryService.updateRegistry(caseData);
    }
}
