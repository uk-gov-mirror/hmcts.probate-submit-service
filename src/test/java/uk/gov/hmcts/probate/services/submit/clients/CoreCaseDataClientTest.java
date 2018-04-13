package uk.gov.hmcts.probate.services.submit.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreCaseDataClientTest {

    @Mock
    private PersistenceEntityBuilder entityBuilder;

    @Mock
    private RestTemplate restTemplate;
    
    @Mock 
    private CoreCaseDataMapper ccdDataMapper;
    
    @InjectMocks
    private CoreCaseDataClient coreCaseDataClient;
          
    String userId;
    String authorizationToken; 
    JsonNode ccdStartCaseResponse;
    private Calendar submissonTimestamp;
    JsonNode sequenceNumber;
        
    @Autowired
    private TestUtils testUtils;
        
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(coreCaseDataClient, "coreCaseDataServiceURL", "http://localhost:9999/citizen/%s/jurisdictions/probate/case-types/probate/cases");
         userId = "123";
         authorizationToken = "dummyToken";
         ccdStartCaseResponse = testUtils.getJsonNodeFromFile("ccdStartCaseResponse.json");

         submissonTimestamp = Calendar.getInstance();
         sequenceNumber = new LongNode(123L);
    }

    @Test
    public void saveSubmissionSuccessTest() {
        HttpEntity<JsonNode> persistenceReq = new HttpEntity<>(new TextNode("requestBody"), new HttpHeaders());
        when(entityBuilder.createCcdSaveRequest(any(),any())).thenReturn(persistenceReq);

        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(new TextNode("responseBody"), HttpStatus.CREATED);
        doReturn(mockResponse).when(restTemplate).exchange(anyString(), eq(HttpMethod.POST), isA(HttpEntity.class), eq(JsonNode.class));

        JsonNode mappedData =  testUtils.getJsonNodeFromFile("mappedData.json");
        doReturn(mappedData).when(ccdDataMapper).createCcdData(any(), any(), any(),any(),any());

        
        coreCaseDataClient.saveCase(persistenceReq.getBody(), userId, authorizationToken, ccdStartCaseResponse, submissonTimestamp, sequenceNumber);

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), isA(HttpEntity.class), eq(JsonNode.class));
    }

    @Test(expected = RestClientException.class)
    public void processFailTest() {
        doThrow(RestClientException.class).when(restTemplate).exchange(anyString(), eq(HttpMethod.POST), isNull(HttpEntity.class), eq(JsonNode.class));
       
        JsonNode mappedData =  testUtils.getJsonNodeFromFile("mappedData.json");
        doReturn(mappedData).when(ccdDataMapper).createCcdData(any(), any(), any(),any(),any());
        
        coreCaseDataClient.saveCase(NullNode.getInstance(), userId, authorizationToken, ccdStartCaseResponse, submissonTimestamp, sequenceNumber);

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), isA(HttpEntity.class), eq(JsonNode.class));
    }
}
