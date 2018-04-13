package uk.gov.hmcts.probate.services.submit.clients;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import uk.gov.hmcts.probate.security.SecurityUtils;


public class PersistenceEntityBuilderTest {

    private TestUtils testUtils;
    private PersistenceEntityBuilder persistenceEntityBuilder;

    @Mock
    private SecurityUtils securityUtils;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        persistenceEntityBuilder = new PersistenceEntityBuilder(securityUtils);
        testUtils = new TestUtils();
        when(securityUtils.authorizationHeaders()).thenReturn(new HttpHeaders());
    }

    @Test
    public void testCreatePersistenceRequest() {
        JsonNode jsonNode = testUtils.getJsonNodeFromFile("formPayload.json");

        HttpEntity<JsonNode> persistenceRequest = persistenceEntityBuilder.createPersistenceRequest(jsonNode);

        assertEquals(persistenceRequest.getBody(), jsonNode);
        assertEquals(persistenceRequest.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
    }
    
    @Test
    public void testCreateCcdSaveRequest() {
        JsonNode jsonNode = testUtils.getJsonNodeFromFile("formPayload.json");
        String authorization = "dummyToken";
        HttpEntity<JsonNode> request = persistenceEntityBuilder.createCcdSaveRequest(jsonNode, authorization);

        assertEquals(request.getBody(), jsonNode);
        assertEquals(request.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
        System.out.println(request.getHeaders().get("Authorization"));
        List<String> auth = new ArrayList<>();
        auth.add("Bearer dummyToken");
        assertEquals(request.getHeaders().get("Authorization"), auth);

    }   
        
    @Test
    public void testCreateCcdStartRequest() {
        String authorization = "dummyToken";
        HttpEntity<JsonNode> request = persistenceEntityBuilder.createCcdStartRequest(authorization);

        assertEquals(request.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
        List<String> auth = new ArrayList<>();
        auth.add("Bearer dummyToken");
        assertEquals(request.getHeaders().get("Authorization"), auth);
    }   
}
