package uk.gov.hmcts.probate.services.submit.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseData;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseInfo;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseType;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftRequest;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftResponse;
import uk.gov.hmcts.probate.services.submit.model.v2.grantofrepresentation.GrantOfRepresentation;
import uk.gov.hmcts.probate.services.submit.services.v2.CoreCaseDataApiClient;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DraftServiceImplTest {

    private static final String APPLICANT_EMAIL = "test@test.com";

    private static final String CASE_ID = "12323213323";
    private static final String STATE = "STATE";

    @Mock
    private SecurityUtils mockSecurityUtils;

    @Mock
    private CoreCaseDataApiClient mockCoreCaseDataApiClient;

    @InjectMocks
    private DraftServiceImpl draftService;

    private DraftRequest draftRequest;

    private CaseData caseData;

    private SecurityDTO securityDTO;

    private CaseInfo caseInfo;

    @Before
    public void setUp() {
        securityDTO = SecurityDTO.builder().build();
        caseData = GrantOfRepresentation.builder().build();
        draftRequest = DraftRequest.builder().caseData(caseData).build();
        caseInfo = CaseInfo.builder().caseId(CASE_ID).state(STATE).build();
    }

    @Test
    public void shouldCreateDraftWhenNoExistingCase() {
        when(mockSecurityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(mockCoreCaseDataApiClient.findCase(APPLICANT_EMAIL, CaseType.GRANT_OF_REPRESENTATION, securityDTO))
                .thenReturn(Optional.empty());
        when(mockCoreCaseDataApiClient.createDraft(caseData, securityDTO)).thenReturn(caseInfo);

        DraftResponse draftResponse = draftService.saveDraft(APPLICANT_EMAIL, draftRequest);

        assertThat(draftResponse.getCaseData(), is(caseData));
        assertThat(draftResponse.getCaseInfo(), is(caseInfo));
        verify(mockSecurityUtils, times(1)).getSecurityDTO();
        verify(mockCoreCaseDataApiClient, times(1)).findCase(APPLICANT_EMAIL, CaseType.GRANT_OF_REPRESENTATION, securityDTO);
        verify(mockCoreCaseDataApiClient, times(1)).createDraft(caseData, securityDTO);
    }

    @Test
    public void shouldUpdateDraftWhenExistingCase() {
        when(mockSecurityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(mockCoreCaseDataApiClient.findCase(APPLICANT_EMAIL, CaseType.GRANT_OF_REPRESENTATION, securityDTO))
                .thenReturn(Optional.of(caseInfo));
        when(mockCoreCaseDataApiClient.updateDraft(CASE_ID, caseData, securityDTO)).thenReturn(caseInfo);

        DraftResponse draftResponse = draftService.saveDraft(APPLICANT_EMAIL, draftRequest);

        assertThat(draftResponse.getCaseData(), is(caseData));
        assertThat(draftResponse.getCaseInfo(), is(equalTo(caseInfo)));
        verify(mockSecurityUtils, times(1)).getSecurityDTO();
        verify(mockCoreCaseDataApiClient, times(1)).findCase(APPLICANT_EMAIL, CaseType.GRANT_OF_REPRESENTATION, securityDTO);
        verify(mockCoreCaseDataApiClient, times(1)).updateDraft(CASE_ID, caseData, securityDTO);
    }
}