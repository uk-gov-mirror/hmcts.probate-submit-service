package uk.gov.hmcts.probate.services.submit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaseDataValidatorFactory {

    private final Map<CaseType, Function<CaseData, CaseDataValidator<? extends CaseData>>> validatorMap;

    public CaseDataValidator getValidator(CaseData caseData) {
        CaseType caseType = CaseType.getCaseType(caseData);
        return Optional.ofNullable(validatorMap.get(caseType)).orElseThrow(IllegalArgumentException::new)
                .apply(caseData);
    }
}
