package uk.gov.hmcts.probate.services.submit.validation;

import com.google.common.collect.ImmutableMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.services.submit.validation.validator.IntestacyValidator;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

import java.util.Map;

import static uk.gov.hmcts.reform.probate.model.cases.CaseType.CAVEAT;
import static uk.gov.hmcts.reform.probate.model.cases.CaseType.GRANT_OF_REPRESENTATION;
import static uk.gov.hmcts.reform.probate.model.cases.CaseType.STANDING_SEARCH;
import static uk.gov.hmcts.reform.probate.model.cases.CaseType.WILL_LODGEMENT;

@Configuration
public class CaseDataValidatorConfiguration {

    @Bean
    public IntestacyValidator intestacyValidator() {
        return new IntestacyValidator();
    }
}
