package uk.gov.hmcts.probate.services.submit.services;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.probate.services.submit.clients.PersistenceClient;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest()
public class SequenceServiceTest {

    @MockBean
    SubmitService submitService;
    @MockBean
    Map<Integer, Registry> registryMap;
    @Mock
    Registry mockRegistry;

    private TestUtils testUtils;
    private PersistenceClient persistenceClient;
    private SequenceService sequenceService;

    @Before
    public void setUp() throws Exception {
        this.persistenceClient = mock(PersistenceClient.class);
        this.testUtils = new TestUtils();
    }

    @Test
    public void nextRegistryDataObjectTest() {
        JsonNode registryData = testUtils.getJsonNodeFromFile("registryData.json");
        String sequenceNumber = "1234";
        when(sequenceService.identifyNextRegistry()).thenReturn(mockRegistry);
        when(sequenceService.getRegistrySequenceNumber(mockRegistry)).thenReturn(10001L);
        when(mockRegistry.getAddress()).thenReturn("Test Address Line 1 \n Test Address Line 2 \n Test Address Postcode");

        JsonNode result = sequenceService.nextRegistryDataObject(sequenceNumber);
        Assert.assertEquals(result, registryData);
    }

    @Test
    public void identifyNextRegistryTest() throws Exception {
        when(this.registryMap.get(0)).thenReturn(mockRegistry);
        when(this.mockRegistry.getName()).thenReturn("oxford");
        Registry testResultOx = this.registryMap.get(0);
        Assert.assertEquals(testResultOx.getName(), "oxford");

        when(this.registryMap.get(1)).thenReturn(mockRegistry);
        when(this.mockRegistry.getName()).thenReturn("birmingham");
        Registry testResultBham = this.registryMap.get(1);
        Assert.assertEquals(testResultBham.getName(), "birmingham");
    }
}
