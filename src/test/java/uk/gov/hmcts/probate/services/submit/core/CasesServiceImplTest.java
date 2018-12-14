package uk.gov.hmcts.probate.services.submit.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;
import uk.gov.hmcts.probate.services.submit.services.v2.CoreCaseDataService;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CasesServiceImplTest {

    private static final String EMAIL_ADDRESS = "test@test.com";

    private static final CaseType CASE_TYPE = CaseType.GRANT_OF_REPRESENTATION;

    @Mock
    private CoreCaseDataService coreCaseDataService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CasesServiceImpl casesService;

    @Test
    public void shouldGetCase() {
        SecurityDTO securityDTO = SecurityDTO.builder().build();
        Optional<CaseResponse> caseResponseOptional = Optional.of(CaseResponse.builder().build());
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(coreCaseDataService.findCase(EMAIL_ADDRESS, CASE_TYPE, securityDTO)).thenReturn(caseResponseOptional);

        CaseResponse caseResponse = casesService.getCase(EMAIL_ADDRESS, CASE_TYPE);

        assertThat(caseResponse, equalTo(caseResponseOptional.get()));
        verify(securityUtils, times(1)).getSecurityDTO();
        verify(coreCaseDataService, times(1)).findCase(EMAIL_ADDRESS, CASE_TYPE, securityDTO);
    }
}
