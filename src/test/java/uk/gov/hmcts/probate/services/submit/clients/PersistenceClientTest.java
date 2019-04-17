package uk.gov.hmcts.probate.services.submit.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.submit.model.FormData;
import static org.mockito.Matchers.anyString;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class PersistenceClientTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private RequestFactory entityBuilder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PersistenceClient persistenceClient;

    @Test
    public void updateFormDataSuccessTest() {
        HttpEntity<JsonNode> persistenceReq = new HttpEntity<>(new TextNode("requestBody"), new HttpHeaders());
        when(entityBuilder.createPersistenceRequest(any())).thenReturn(persistenceReq);

        persistenceClient.updateFormData("emailId", Long.parseLong("123456789"), new TextNode("requestBody"));

        verify(restTemplate, times(1)).put(endsWith("/emailId"), eq(persistenceReq));
    }

    @Test
    public void loadFormDataByIdSuccessTest() {
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(new TextNode("response"), HttpStatus.CREATED);
        doReturn(mockResponse).when(restTemplate).getForEntity(endsWith("/emailId"), eq(JsonNode.class));

        FormData actualResponse = persistenceClient.loadFormDataById("emailId");

        verify(restTemplate, times(1)).getForEntity(endsWith("/emailId"), eq(JsonNode.class));
        assertEquals(actualResponse.getJson(), mockResponse.getBody());
    }

    @Test
    public void getNextSequenceNumber() {
        ResponseEntity<Long> mockResponse = new ResponseEntity<>(1234l, HttpStatus.CREATED);
        doReturn(mockResponse).when(restTemplate).getForEntity(endsWith("/RegistryName"), eq(Long.class));

        Long result = persistenceClient.getNextSequenceNumber("RegistryName");
        verify(restTemplate, times(1)).getForEntity(endsWith("/RegistryName"), eq(Long.class));
        assertEquals(result, mockResponse.getBody());
    }


    @Test(expected = HttpClientErrorException.class)
    public void shouldThrowUpdateFormDataSuccessTest() {
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "{}")).when(restTemplate).put(anyString(), any());
        HttpEntity<JsonNode> persistenceReq = new HttpEntity<>(new TextNode("requestBody"), new HttpHeaders());
        when(entityBuilder.createPersistenceRequest(any())).thenReturn(persistenceReq);

        persistenceClient.updateFormData("emailId", Long.parseLong("123456789"), new TextNode("requestBody"));
    }
}