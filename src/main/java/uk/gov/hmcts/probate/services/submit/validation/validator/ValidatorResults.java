package uk.gov.hmcts.probate.services.submit.validation.validator;

import java.util.ArrayList;
import java.util.List;

public class ValidatorResults {

    private List<String> validationMessages = new ArrayList<>();

    public ValidatorResults() {
    }

    public List<String> getValidationMessages() {
        return validationMessages;
    }

    public void setValidationMessages(List<String> validationMessages) {
        this.validationMessages = validationMessages;
    }

}
