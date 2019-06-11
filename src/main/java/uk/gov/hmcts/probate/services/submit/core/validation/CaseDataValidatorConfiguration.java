package uk.gov.hmcts.probate.services.submit.core.validation;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class CaseDataValidatorConfiguration {

    @Autowired
    @Qualifier("IntestacyRule")
    List<ValidationRule<? extends CaseData>> intestacyRules;

    @Autowired
    @Qualifier("CaveatRule")
    List<ValidationRule<? extends CaseData>> caveatRules;

//    @Autowired
//    @Qualifier("PaRule")
//    List<ValidationRule<? extends CaseData>> paRules;


    @Bean
    public Map<CaseType, Function<CaseData, CaseDataValidator<? extends CaseData>>> validatorMap() {
        return ImmutableMap.<CaseType, Function<CaseData, CaseDataValidator<? extends CaseData>>>builder()
                .put(CaseType.CAVEAT, caseData -> new CaseDataValidator(caveatRules))
                .put(CaseType.GRANT_OF_REPRESENTATION, getGrantOfRepresentationValidator())
                .build();
    }

    private Function<CaseData, CaseDataValidator<? extends CaseData>> getGrantOfRepresentationValidator() {
        return caseData -> {
            GrantOfRepresentationData grantOfRepresentationData = (GrantOfRepresentationData) caseData;
            return grantTypeValidatorMap().get(grantOfRepresentationData.getGrantType());
        };
    }

    @Bean
    public Map<GrantType, CaseDataValidator<? extends CaseData>> grantTypeValidatorMap() {
        return ImmutableMap.<GrantType, CaseDataValidator<? extends CaseData>>builder()
                .put(GrantType.INTESTACY, new CaseDataValidator(intestacyRules))
                //.put(GrantType.GRANT_OF_PROBATE, new CaseDataValidator(paRules))
                .build();
    }
}
