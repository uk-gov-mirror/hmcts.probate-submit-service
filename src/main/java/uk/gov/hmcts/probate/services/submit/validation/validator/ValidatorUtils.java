package uk.gov.hmcts.probate.services.submit.validation.validator;

public class ValidatorUtils {

    public static Boolean allValuesNotNull(Object... values) {
        Object[] valuesList = values;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                return false;
            }
        }
        return true;
    }

}
