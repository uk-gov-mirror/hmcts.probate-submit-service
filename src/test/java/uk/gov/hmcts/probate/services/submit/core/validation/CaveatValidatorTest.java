package uk.gov.hmcts.probate.services.submit.core.validation;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.probate.model.cases.ValidatorResults;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

import java.time.LocalDate;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CaveatValidatorTest {

    private CaveatData caveatData;
    private CaseDataValidator<CaveatData> caveatValidator;

    LocalDate afterDate = LocalDate.of(2018, 9, 12);
    LocalDate beforeDate = LocalDate.of(1954, 9, 18);

    @Autowired
    private CaseDataValidatorFactory caseDataValidatorFactory;

    @Before
    public void setUpTest() {
        caveatData = CaveatCreator.createCaveatCase();
        caveatValidator = caseDataValidatorFactory.getValidator(caveatData);
    }


    @Test
    public void shouldFailValidationWhenDodIsBeforeDob() {
        caveatData.setDeceasedDateOfBirth(afterDate);
        caveatData.setDeceasedDateOfDeath(beforeDate);
        ValidatorResults validateResults = caveatValidator.validate(caveatData);
        assertValidationErrorMessage(validateResults, "DeceasedDateOfDeath before DeceasedDateOfBirth");
    }

    @Test
    public void shouldNotRaiseErrorWhenDobIsNull() {
        caveatData.setDeceasedDateOfBirth(null);
        caveatData.setDeceasedDateOfDeath(afterDate);
        ValidatorResults validateResults = caveatValidator.validate(caveatData);
        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }

    @Test
    public void shouldNotRaiseErrorWhenDodIsAfterDob() {
        caveatData.setDeceasedDateOfBirth(beforeDate);
        caveatData.setDeceasedDateOfDeath(afterDate);
        ValidatorResults validateResults = caveatValidator.validate(caveatData);
        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }

    private void assertValidationErrorMessage(ValidatorResults validateResults, String validationMessage) {
        Assertions.assertThat(validateResults.getValidationMessages()).isNotEmpty();
        Assertions.assertThat(validateResults.getValidationMessages()).contains(validationMessage);
    }
}
