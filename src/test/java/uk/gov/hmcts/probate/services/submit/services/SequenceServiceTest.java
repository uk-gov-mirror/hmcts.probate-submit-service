package uk.gov.hmcts.probate.services.submit.services;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.probate.services.submit.clients.PersistenceClient;

import static org.mockito.Matchers.anyInt;
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

    private PersistenceClient persistenceClient;

    @Test
    public void testGetRegistrySequenceNumber() throws Exception {
        persistenceClient = mock(PersistenceClient.class);
        when(persistenceClient.getNextSequenceNumber(anyString())).thenReturn(1234L);
    }

    @Test
    public void testIdentifyNextRegistry() throws Exception {
        when(this.registryMap.get(anyInt())).thenReturn(mockRegistry);
        when(this.mockRegistry.getName()).thenReturn("oxford");

        Registry testResult = this.registryMap.get(0);
        Assert.assertEquals(testResult.getName(), "oxford");
    }

    @Test
    public void testGetRegistryDataObject() throws Exception {
    }
}
