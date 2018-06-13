package uk.gov.hmcts.probate.services.submit.services;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.probate.services.submit.clients.PersistenceClient;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;

@RunWith(MockitoJUnitRunner.class)
public class SequenceServiceTest {
    @Mock
    private Map<Integer, Registry> registryMap;
    private SubmitService submitService;
    private PersistenceClient persistenceClient;
    private JavaMailSenderImpl mailSender;
    private ObjectMapper mapper;
    private Registry mockRegistry;
    private SequenceService sequenceService;
    private TestUtils testUtils;
    private long submissionReference;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        submitService = mock(SubmitService.class);
        persistenceClient = mock(PersistenceClient.class);
        mailSender = mock(JavaMailSenderImpl.class);
        mockRegistry = mock(Registry.class);
        mapper = new ObjectMapper();
        testUtils = new TestUtils();
        sequenceService = new SequenceService(registryMap, persistenceClient, mailSender, mapper);
        submissionReference = 1234;
    }

    @Test
    public void nextRegistry() {
        JsonNode registryData = testUtils.getJsonNodeFromFile("registryDataSubmit.json");
        when(mockRegistry.capitalizeRegistryName()).thenReturn("Oxford");
        when(persistenceClient.getNextSequenceNumber("oxford")).thenReturn(1234L);
        when(sequenceService.getRegistrySequenceNumber(mockRegistry)).thenReturn(20013L);
        when(mockRegistry.getEmail()).thenReturn("oxford@email.com");
        when(mockRegistry.getAddress()).thenReturn("Test Address Line 1\nTest Address Line 2\nTest Address Postcode");

        JsonNode result = sequenceService.populateRegistrySubmitData(submissionReference, mockRegistry);
        assertEquals(result.toString(), registryData.toString());
    }

    @Test
    public void populateRegistrySubmitData() {
        when(registryMap.size()).thenReturn(2);
        when(registryMap.get(anyInt())).thenReturn(mockRegistry);

        JsonNode registryData = testUtils.getJsonNodeFromFile("registryDataSubmit.json");
        when(sequenceService.identifyNextRegistry()).thenReturn(mockRegistry);
        when(mockRegistry.capitalizeRegistryName()).thenReturn("Oxford");
        when(persistenceClient.getNextSequenceNumber("oxford")).thenReturn(1234L);
        when(sequenceService.getRegistrySequenceNumber(mockRegistry)).thenReturn(20013L);
        when(mockRegistry.getEmail()).thenReturn("oxford@email.com");
        when(mockRegistry.getAddress()).thenReturn("Test Address Line 1\nTest Address Line 2\nTest Address Postcode");

        JsonNode response = sequenceService.populateRegistrySubmitData(submissionReference, mockRegistry);
        assertEquals(response.toString(), registryData.toString());
    }

    @Test
    public void populateRegistryResubmitDataNewApplication() {
        JsonNode registryData = testUtils.getJsonNodeFromFile("registryDataResubmitNewApplication.json");
        JsonNode formData = testUtils.getJsonNodeFromFile("formData.json");
        JsonNode response = sequenceService.populateRegistryResubmitData(submissionReference, formData);
        assertEquals(response.toString(), registryData.toString());
    }

    @Test
    public void populateRegistryResubmitDataOldApplication() {
        Properties messageProperties = new Properties();
        messageProperties.put("recipient", "oxford@email.com");
        JsonNode registryData = testUtils.getJsonNodeFromFile("registryDataResubmitOldApplication.json");
        JsonNode formData = testUtils.getJsonNodeFromFile("formDataOldApplication.json");
        when(mailSender.getJavaMailProperties()).thenReturn(messageProperties);

        JsonNode response = sequenceService.populateRegistryResubmitData(submissionReference, formData);
        assertEquals(response.toString(), registryData.toString());
    }

    @Test
    public void identifyNextRegistry() {
        when(registryMap.size()).thenReturn(2);
        when(registryMap.get(anyInt())).thenReturn(mockRegistry);

        Registry result = sequenceService.identifyNextRegistry();
        assertThat(result, is(equalTo(mockRegistry)));
    }
}
