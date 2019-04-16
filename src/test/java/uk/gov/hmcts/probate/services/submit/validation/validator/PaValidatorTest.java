package uk.gov.hmcts.probate.services.submit.validation.validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.ValidatorResults;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.ArrayList;
import java.util.List;

public class PaValidatorTest {

    private List<String> errors = new ArrayList<>();
    private GrantOfRepresentationData grantOfRepresentationData;
    PaValidator paValidator = new PaValidator();

    private ProbateCaseDetails caseResponse;

    @Before
    public void setUpTest() {
        grantOfRepresentationData = GrantOfRepresentationCreator.createPaCase();
    }

    @Test
    public void shouldValidateSuccessfully() {
        ValidatorResults validateResults = paValidator.validate(grantOfRepresentationData);
        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }
}
