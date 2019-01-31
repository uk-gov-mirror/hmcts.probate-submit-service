package uk.gov.hmcts.probate.services.submit.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseNotFoundException;
import uk.gov.hmcts.probate.services.submit.services.CoreCaseDataService;
import uk.gov.hmcts.probate.services.submit.validation.CaseDataValidatorFactory;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.SubmitResult;

import java.util.Optional;

@Slf4j
public abstract class AbstractSubmissionsService {

    private SecurityUtils securityUtils = null;
    private SearchFieldFactory searchFieldFactory = null;
    private CaseDataValidatorFactory caseDataValidatorFactory = null;
    private CoreCaseDataService coreCaseDataService;

    public AbstractSubmissionsService(SecurityUtils securityUtils, SearchFieldFactory searchFieldFactory, CaseDataValidatorFactory caseDataValidatorFactory, CoreCaseDataService coreCaseDataService) {
        this.securityUtils = securityUtils;
        this.searchFieldFactory = searchFieldFactory;
        this.caseDataValidatorFactory = caseDataValidatorFactory;
        this.coreCaseDataService = coreCaseDataService;
    }

    public SubmitResult process(String identifier, ProbateCaseDetails caseRequest) {
        log.info("Processing case type: {}", caseRequest.getCaseData().getClass().getSimpleName());
        CaseData caseData = caseRequest.getCaseData();
        CaseType caseType = CaseType.getCaseType(caseData);
        SubmitResult submitResult = new SubmitResult();
        validateCase(caseData, submitResult);
        if (submitResult.getValidatorResults().isValid()) {
            assertIndentifierMatchesCase(identifier, caseData, caseType);
            SecurityDTO securityDTO = securityUtils.getSecurityDTO();
            processCase(identifier, caseData, caseType, securityDTO);
        } else {
            submitResult.setProbateCaseDetails(caseRequest);
        }
        return submitResult;
    }

    abstract  ProbateCaseDetails processCase(String identifier, CaseData caseData, CaseType caseType, SecurityDTO securityDTO);


    protected ProbateCaseDetails findCase(String searchField, CaseType caseType, SecurityDTO securityDTO) {
        Optional<ProbateCaseDetails> caseResponseOptional = coreCaseDataService.
                findCase(searchField, caseType, securityDTO);
        return caseResponseOptional.orElseThrow(CaseNotFoundException::new);
    }

    private void assertIndentifierMatchesCase(String identifier, CaseData caseData, CaseType caseType) {
        String searchFieldValueInBody = searchFieldFactory.getSearchFieldValuePair(caseType, caseData).getRight();
        Assert.isTrue(searchFieldValueInBody.equals(identifier), "Applicant email on path must match case data");
    }

    private void validateCase(CaseData caseData, SubmitResult submitResult) {
        caseDataValidatorFactory.getValidator(caseData).ifPresent(caseDataValidator -> {
            submitResult.setValidatorResults(caseDataValidator.validate(caseData));
        });
    }


}
