package uk.gov.hmcts.probate.services.submit.validation.validator;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.submit.validation.ValidationResult;
import uk.gov.hmcts.probate.services.submit.validation.ValidationRule;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.ValidatorResults;

import java.util.List;
import java.util.stream.Collectors;

@Component
public interface CaseDataValidator<C extends CaseData> {


    default ValidatorResults validate(C caseData) {
        return new ValidatorResults(getRules().stream().map(rule -> rule.test(caseData))
            .filter(validationResult -> !validationResult.isValid())
            .map(ValidationResult::getMessage)
            .collect(Collectors.toList()));
    }

    List<ValidationRule<C>> getRules();
}
