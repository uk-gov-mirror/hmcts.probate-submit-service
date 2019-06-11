package uk.gov.hmcts.probate.services.submit.core.validation.rules;

public final class ValidatorUtils {

    private ValidatorUtils(){
    }

    public static Boolean allValuesNotNull(Object... values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                return false;
            }
        }
        return true;
    }

}
