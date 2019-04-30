package uk.gov.hmcts.probate.services.submit.validation;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import uk.gov.hmcts.probate.services.submit.validation.rules.ValidatorUtils;


public class ValidatorUtilsTest {

    @Test
    public void shouldReturnTrueIfAllValuesNotNull() {
        Boolean result = ValidatorUtils.allValuesNotNull("blah", "blah");
        Assertions.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseIfaValuesNull() {
        Boolean result = ValidatorUtils.allValuesNotNull("blah", null);
        Assertions.assertFalse(result);
    }


}