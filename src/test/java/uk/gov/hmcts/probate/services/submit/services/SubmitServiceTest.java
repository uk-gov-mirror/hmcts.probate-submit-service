package uk.gov.hmcts.probate.services.submit.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.probate.services.submit.clients.MailClient;
import uk.gov.hmcts.probate.services.submit.clients.PersistenceClient;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import uk.gov.hmcts.probate.services.submit.clients.CoreCaseDataClient;

public class SubmitServiceTest {

    private TestUtils testUtils;

    private SubmitService submitService;
    private MailClient mockMailClient;
    private PersistenceClient persistenceClient;
    private CoreCaseDataClient coreCaseDataClient;
    private SequenceService sequenceService;
    private Calendar submissionTimestamp;

    @Before
    public void setUp() throws Exception {
        testUtils = new TestUtils();
        persistenceClient = mock(PersistenceClient.class);
        mockMailClient = mock(MailClient.class);
        coreCaseDataClient = mock(CoreCaseDataClient.class);
        sequenceService = mock(SequenceService.class);
        submitService = new SubmitService(mockMailClient, persistenceClient, coreCaseDataClient, sequenceService);
        submissionTimestamp = Calendar.getInstance();
    }

    @Test
    public void testSubmitWithSuccess() {
        String userId = "123";
        String authorizationToken = "dummyAuthToken";
        JsonNode submitData = testUtils.getJsonNodeFromFile("formPayload.json");
        JsonNode registryData = testUtils.getJsonNodeFromFile("registryData.json");

        when(persistenceClient.loadFormData(anyString())).thenReturn(submitData);
        when(persistenceClient.saveSubmission(submitData)).thenReturn(submitData);
        when(mockMailClient.execute(submitData, submitData.get("id").asLong(), submissionTimestamp)).thenReturn("12345678");
        when(sequenceService.nextRegistryDataObject(submitData.get("id").asText())).thenReturn(registryData);
        JsonNode dummmyCcdStartCaseRespose =  testUtils.getJsonNodeFromFile("ccdStartCaseResponse.json");

        JsonNode response = submitService.submit(submitData, userId, authorizationToken);

        assertThat(response, is(registryData));
    }

    @Test
    public void testResubmitWithSuccess() {
        JsonNode resubmitData = testUtils.getJsonNodeFromFile("formPayload.json");
        when(persistenceClient.loadSubmission(Long.parseLong("112233"))).thenReturn(resubmitData);
        when(mockMailClient.execute(eq(resubmitData), eq(112233L), any(Calendar.class) )).thenReturn("12345678");

        String response = submitService.resubmit(Long.parseLong("112233"));

        assertThat(response, is("12345678"));
    }
}