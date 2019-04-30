package uk.gov.hmcts.probate.services.submit.validation;

import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.ValidatorResults;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CaseDataValidator<C extends CaseData> {

    private final List<ValidationRule<C>> rules;

    public ValidatorResults validate(C caseData) {
        return new ValidatorResults(rules.stream()
                .map(rule -> rule.test(caseData))
                .filter(validationResult -> !validationResult.isValid())
                .map(ValidationResult::getMessage)
                .collect(Collectors.toList()));
    }
}
