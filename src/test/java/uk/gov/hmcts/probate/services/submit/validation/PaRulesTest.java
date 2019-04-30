package uk.gov.hmcts.probate.services.submit.validation;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.services.submit.validation.rules.PaRules;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.ArrayList;
import java.util.List;

public class PaRulesTest {

    private List<String> errors = new ArrayList<>();
    private GrantOfRepresentationData grantOfRepresentationData;
    PaRules paRules = new PaRules();

    private ProbateCaseDetails caseResponse;

    @Before
    public void setUpTest() {
        grantOfRepresentationData = GrantOfRepresentationCreator.createPaCase();
    }

    @Test
    public void shouldValidateSuccessfully() {
//        ValidatorResults validateResults = paRules//.validate(grantOfRepresentationData);
//        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }
}
