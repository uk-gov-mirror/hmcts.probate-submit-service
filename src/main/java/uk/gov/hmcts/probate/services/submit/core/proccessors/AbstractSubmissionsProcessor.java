package uk.gov.hmcts.probate.services.submit.core.proccessors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.core.SearchFieldFactory;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseNotFoundException;
import uk.gov.hmcts.probate.services.submit.services.CoreCaseDataService;
import uk.gov.hmcts.probate.services.submit.validation.CaseDataValidatorFactory;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.SubmitResult;
import uk.gov.hmcts.reform.probate.model.cases.ValidatorResults;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Component
public abstract class AbstractSubmissionsProcessor {

    protected final SecurityUtils securityUtils;
    private final SearchFieldFactory searchFieldFactory;
    private final CaseDataValidatorFactory caseDataValidatorFactory;
    private final CoreCaseDataService coreCaseDataService;

    public SubmitResult process(String identifier, Supplier<ProbateCaseDetails> caseRequestSupplier) {
        ProbateCaseDetails caseRequest = caseRequestSupplier.get();
        log.info("Processing case type: {}", caseRequest.getCaseData().getClass().getSimpleName());
        CaseData caseData = caseRequest.getCaseData();
        CaseType caseType = CaseType.getCaseType(caseData);
        assertIdentifierMatchesCase(identifier, caseData, caseType);
        ValidatorResults validatorResults = validateCase(caseData);
        return SubmitResult.builder()
                .probateCaseDetails(isValid(validatorResults) ?  processCase(identifier, caseData) : caseRequest)
                .validatorResults(validatorResults)
                .build();
    }

    private Boolean isValid(ValidatorResults validatorResults) {
        return validatorResults.getValidationMessages().isEmpty();
    }

    protected abstract ProbateCaseDetails processCase(String identifier, CaseData caseData);

    protected ProbateCaseDetails findCase(String searchField, CaseType caseType, SecurityDTO securityDTO) {
        Optional<ProbateCaseDetails> caseResponseOptional = coreCaseDataService.
                findCase(searchField, caseType, securityDTO);
        return caseResponseOptional.orElseThrow(CaseNotFoundException::new);
    }

    private void assertIdentifierMatchesCase(String identifier, CaseData caseData, CaseType caseType) {
        String searchFieldValueInBody = searchFieldFactory.getSearchFieldValuePair(caseType, caseData).getRight();
        Assert.isTrue(searchFieldValueInBody.equals(identifier), "Application id email on path must match case data");
    }

    private ValidatorResults validateCase(CaseData caseData) {
        return caseDataValidatorFactory.getValidator(caseData).validate(caseData);
    }
}
