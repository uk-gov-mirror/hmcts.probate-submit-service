package uk.gov.hmcts.probate.services.submit.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.probate.model.PaymentStatus;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CaseSubmissionUpdaterTest {

    @Mock
    private RegistryService registryService;

    @InjectMocks
    private CaseSubmissionUpdater caseSubmissionUpdater;

    @Test
    public void shouldUpdateGrantOfRepresentationWhenPaymentSuccess() {
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .grantType(GrantType.GRANT_OF_PROBATE)
            .build();

        caseSubmissionUpdater.updateCaseForSubmission(grantOfRepresentationData, PaymentStatus.SUCCESS);

        verify(registryService, times(1)).updateRegistry(grantOfRepresentationData);
    }

    @Test
    public void shouldNotUpdateGrantOfRepresentationWhenPaymentFailed() {
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .grantType(GrantType.GRANT_OF_PROBATE)
            .build();

        caseSubmissionUpdater.updateCaseForSubmission(grantOfRepresentationData, PaymentStatus.FAILED);

        verify(registryService, never()).updateRegistry(grantOfRepresentationData);
    }

    @Test
    public void shouldUpdateCaveatWhenPaymentSuccess() {
        CaveatData caveatData = CaveatData.builder().build();

        caseSubmissionUpdater.updateCaseForSubmission(caveatData, PaymentStatus.SUCCESS);

        verify(registryService, times(1)).updateRegistry(caveatData);
    }

    @Test
    public void shouldNotUpdateCaveatWhenPaymentFailed() {
        CaveatData caveatData = CaveatData.builder().build();

        caseSubmissionUpdater.updateCaseForSubmission(caveatData, PaymentStatus.FAILED);

        verify(registryService, never()).updateRegistry(caveatData);
    }
}
