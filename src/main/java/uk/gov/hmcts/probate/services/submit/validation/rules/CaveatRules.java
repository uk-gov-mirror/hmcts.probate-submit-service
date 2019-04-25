package uk.gov.hmcts.probate.services.submit.validation.rules;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.services.submit.validation.ValidationRule;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

@Configuration
public class CaveatRules {

    @Bean
    @Qualifier("CaveatRule")
    public ValidationRule<CaveatData> isCaveatDeceasedDateOfDeathAfterDateOfBirth() {
        return ValidationRule.from(caveat -> ValidatorUtils.allValuesNotNull(caveat.getDeceasedDateOfBirth(), caveat.getDeceasedDateOfDeath()) &&
                        caveat.getDeceasedDateOfDeath().isBefore(caveat.getDeceasedDateOfBirth())
                , "DeceasedDateOfDeath before DeceasedDateOfBirth");
    }

}
