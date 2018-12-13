package uk.gov.hmcts.probate.services.submit.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseNotFoundException;
import uk.gov.hmcts.probate.services.submit.services.v2.CasesService;
import uk.gov.hmcts.probate.services.submit.services.v2.CoreCaseDataService;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CasesServiceImpl implements CasesService {

    private final CoreCaseDataService coreCaseDataService;

    private final SecurityUtils securityUtils;

    @Override
    public CaseResponse getCase(String applicantEmail, CaseType caseType) {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        Optional<CaseResponse> caseResponseOptional = coreCaseDataService
                .findCase(applicantEmail, caseType, securityDTO);
        return caseResponseOptional.orElseThrow(() -> new CaseNotFoundException());
    }
}
