package uk.gov.hmcts.probate.services.submit.services;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.probate.services.submit.clients.PersistenceClient;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest()
public class SequenceServiceTest {
    @MockBean
    SubmitService submitService;
    @Mock
    Registry mockRegistry;
    @Mock
    private PersistenceClient persistenceClient;
    @Mock
    Map<Integer, Registry> registryMap;
    @Mock
    ObjectMapper mapper;
    @InjectMocks
    private SequenceService sequenceService;
    private TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        testUtils = new TestUtils();
        MockitoAnnotations.initMocks(this);
        int mockRegistryCounter = 1;
        when(registryMap.size()).thenReturn(2);
        when(registryMap.get(mockRegistryCounter % registryMap.size()))
                .thenReturn(mockRegistry);

    }

    @Test
    public void nextRegistryData() {
        JsonNode registryData = testUtils.getJsonNodeFromFile("registryDataSubmit.json");
        long sequenceNumber = 1234L;
        when(sequenceService.identifyNextRegistry()).thenReturn(mockRegistry);
        when(mockRegistry.capitalizeRegistryName()).thenReturn("Oxford");
        when(persistenceClient.getNextSequenceNumber("oxford")).thenReturn(1234L);
        when(sequenceService.getRegistrySequenceNumber(mockRegistry)).thenReturn(20013L);
        when(mockRegistry.getEmail()).thenReturn("oxford@email.com");
        when(mockRegistry.getAddress()).thenReturn("Test Address Line 1\nTest Address Line 2\nTest Address Postcode");

        JsonNode response = sequenceService.nextRegistryData(sequenceNumber);
        assertThat(response, is(equalTo(registryData)));
    }

    @Test
    public void createRegistryDataObject() {
        JsonNode registryData = testUtils.getJsonNodeFromFile("registryDataResubmit.json");
        JsonNode formData = testUtils.getJsonNodeFromFile("formData.json");
        long submissionReference = 1234;
        JsonNode response = sequenceService.createRegistryDataObject(submissionReference, formData);
        assertThat(response, is(equalTo(registryData)));
    }

    @Test
    public void identifyNextRegistry() {
        Registry result = sequenceService.identifyNextRegistry();
        assertThat(result, is(equalTo(mockRegistry)));
    }
}
