package uk.gov.hmcts.probate.services.submit.validation.validator;

import uk.gov.hmcts.probate.services.submit.validation.ValidationRule;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.Arrays;
import java.util.List;

public class PaValidator implements CaseDataValidator<GrantOfRepresentationData> {

    @Override
    public List<ValidationRule<GrantOfRepresentationData>> getRules() {
        return Arrays.asList();
    }
}
