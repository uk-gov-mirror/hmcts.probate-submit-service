package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseData;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseInfo;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseType;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CoreCaseDataFacadeImplTest {

    private static final Long CASE_ID = 123456789L;

    private static final String APPLICANT_EMAIL = "test@test.com";

    @Mock
    private CcdClientApi ccdClientApi;

    @InjectMocks
    private CoreCaseDataFacadeImpl coreCaseDataApiFacade;

    @Mock
    private SecurityDTO securityDTO;

    @Mock
    private CaseData caseData;

    @Mock
    private CaseInfo caseInfo;

    @Test
    public void shouldCreateDraft() {
        when(ccdClientApi.createCase(caseData, EventId.CREATE_DRAFT, securityDTO)).thenReturn(caseInfo);

        CaseInfo caseInfo = coreCaseDataApiFacade.createDraft(caseData, securityDTO);

        assertThat(caseInfo, is(equalTo(caseInfo)));
        verify(ccdClientApi, times(1)).createCase(caseData, EventId.CREATE_DRAFT, securityDTO);
    }

    @Test
    public void shouldUpdateDraft() {
        when(ccdClientApi.updateCase(CASE_ID.toString(), caseData, EventId.UPDATE_DRAFT, securityDTO))
                .thenReturn(caseInfo);

        CaseInfo caseInfo = coreCaseDataApiFacade.updateDraft(CASE_ID.toString(), caseData, securityDTO);

        assertThat(caseInfo, is(equalTo(caseInfo)));
        verify(ccdClientApi, times(1)).updateCase(CASE_ID.toString(), caseData, EventId.UPDATE_DRAFT, securityDTO);
    }

    @Test
    public void shouldFindCase() {
        when(ccdClientApi.findCase(APPLICANT_EMAIL, CaseType.GRANT_OF_REPRESENTATION, securityDTO))
                .thenReturn(Optional.of(caseInfo));

        Optional<CaseInfo> caseInfoOptional = coreCaseDataApiFacade.findCase(APPLICANT_EMAIL, CaseType.GRANT_OF_REPRESENTATION, securityDTO);

        assertThat(caseInfo, is(equalTo(caseInfoOptional.get())));
        verify(ccdClientApi, times(1)).findCase(APPLICANT_EMAIL, CaseType.GRANT_OF_REPRESENTATION, securityDTO);
    }
}
