package uk.gov.hmcts.probate.services.submit.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseRequest;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;
import uk.gov.hmcts.probate.services.submit.services.v2.CoreCaseDataService;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseInfo;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentation;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.services.submit.clients.v2.ccd.EventId.CREATE_DRAFT;
import static uk.gov.hmcts.reform.probate.model.cases.CaseType.GRANT_OF_REPRESENTATION;

@RunWith(MockitoJUnitRunner.class)
public class DraftServiceImplTest {

    private static final String APPLICANT_EMAIL = "test@test.com";

    private static final String CASE_ID = "12323213323";
    private static final String STATE = "STATE";

    @Mock
    private SecurityUtils mockSecurityUtils;

    @Mock
    private CoreCaseDataService coreCaseDataService;

    @InjectMocks
    private DraftServiceImpl draftService;

    private CaseRequest caseRequest;

    private CaseData caseData;

    private SecurityDTO securityDTO;

    private CaseInfo caseInfo;

    private CaseResponse caseResponse;

    @Before
    public void setUp() {
        securityDTO = SecurityDTO.builder().build();
        caseData = new GrantOfRepresentation();
        caseRequest = CaseRequest.builder().caseData(caseData).build();
        caseInfo = new CaseInfo();
        caseInfo.setCaseId(CASE_ID);
        caseInfo.setState(STATE);
        caseResponse = CaseResponse.builder().caseData(caseData).caseInfo(caseInfo).build();
    }

    @Test
    public void shouldCreateDraftWhenNoExistingCase() {
        when(mockSecurityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(coreCaseDataService.findCase(APPLICANT_EMAIL, GRANT_OF_REPRESENTATION, securityDTO))
                .thenReturn(Optional.empty());
        when(coreCaseDataService.createCase(caseData, CREATE_DRAFT, securityDTO)).thenReturn(caseResponse);

        CaseResponse caseResponse = draftService.saveDraft(APPLICANT_EMAIL, caseRequest);

        assertThat(caseResponse.getCaseData(), is(caseData));
        assertThat(caseResponse.getCaseInfo(), is(caseInfo));
        verify(mockSecurityUtils, times(1)).getSecurityDTO();
        verify(coreCaseDataService, times(1)).findCase(APPLICANT_EMAIL, GRANT_OF_REPRESENTATION, securityDTO);
        verify(coreCaseDataService, times(1)).createCase(caseData, CREATE_DRAFT, securityDTO);
    }

    @Test
    public void shouldUpdateDraftWhenExistingCase() {
        when(mockSecurityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(coreCaseDataService.findCase(APPLICANT_EMAIL, GRANT_OF_REPRESENTATION, securityDTO))
                .thenReturn(Optional.of(caseResponse));
        when(coreCaseDataService.updateCase(CASE_ID, caseData, CREATE_DRAFT, securityDTO)).thenReturn(caseResponse);

        CaseResponse caseResponse = draftService.saveDraft(APPLICANT_EMAIL, caseRequest);

        assertThat(caseResponse.getCaseData(), is(caseData));
        assertThat(caseResponse.getCaseInfo(), is(equalTo(caseInfo)));
        verify(mockSecurityUtils, times(1)).getSecurityDTO();
        verify(coreCaseDataService, times(1)).findCase(APPLICANT_EMAIL, GRANT_OF_REPRESENTATION, securityDTO);
        verify(coreCaseDataService, times(1)).updateCase(CASE_ID, caseData, CREATE_DRAFT, securityDTO);
    }
}