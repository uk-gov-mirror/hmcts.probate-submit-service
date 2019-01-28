package uk.gov.hmcts.probate.services.submit.validation;

import uk.gov.hmcts.probate.services.submit.validation.validator.CaseDataValidator;
import uk.gov.hmcts.probate.services.submit.validation.validator.IntestacyValidator;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.util.Optional;

public class CaseDataValidatorFactory {

    public static Optional<CaseDataValidator> getInstance(CaseData caseData){

        CaseDataValidator caseDataValidator = null;
        if(CaseType.getCaseType(caseData).equals(CaseType.GRANT_OF_REPRESENTATION)){
           GrantOfRepresentationData gop=  (GrantOfRepresentationData)caseData;
           if(gop.getGrantType().equals(GrantType.INTESTACY))
               caseDataValidator = new IntestacyValidator();
        }
       return  Optional.of(caseDataValidator);
    }
}
