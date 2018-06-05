package uk.gov.hmcts.probate.services.submit.health;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

@RunWith(MockitoJUnitRunner.class)
public class SubmitHealthIndicatorTest {

    private static final String URL = "http://url.com";

    @Mock
    private RestTemplate mockRestTemplate;
   
    @Mock
    private ResponseEntity<String> mockResponseEntity;
    
    private SubmitHealthIndicator submitHealthIndicator;
    
    @Before
    public void setUp() {
    	submitHealthIndicator = new SubmitHealthIndicator(URL, mockRestTemplate);
    }

	@Test
    public void shouldReturnStatusOfUpWhenHttpStatusIsOK() {
        when(mockRestTemplate.getForEntity(URL + "/health", String.class)).thenReturn(mockResponseEntity);      
        when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        Health health = submitHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.UP));
    }

	@Test
    public void shouldReturnStatusOfDownWhenHttpStatusIsNotOK() {
        when(mockRestTemplate.getForEntity(URL + "/health", String.class)).thenReturn(mockResponseEntity);
        when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        when(mockResponseEntity.getStatusCodeValue()).thenReturn(HttpStatus.NO_CONTENT.value());
        Health health = submitHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("error"), is("HTTP Status: 204"));
    }

    @Test
    public void shouldReturnStatusOfDownWhenResourceAccessExceptionIsThrown() {
        final String message = "EXCEPTION MESSAGE";
        when(mockRestTemplate.getForEntity(URL + "/health", String.class)).thenThrow(new ResourceAccessException(message));
        Health health = submitHealthIndicator.health();
        
        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("error"), is("Connection failed with ResourceAccessException"));
    }

    @Test
    public void shouldReturnStatusOfDownWhenHttpStatusCodeExceptionIsThrown() {
        when(mockRestTemplate.getForEntity(URL + "/health", String.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Health health = submitHealthIndicator.health();
        
        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("error"), is("HTTP Status: 400"));
    }

    @Test
    public void shouldReturnStatusOfDownWhenUnknownHttpStatusCodeExceptionIsThrown() {
        final String statusText = "status text";
        when(mockRestTemplate.getForEntity(URL + "/health", String.class))
                .thenThrow(new UnknownHttpStatusCodeException(1000, statusText, null, null, null));
        Health health = submitHealthIndicator.health();
        
        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("error"), is("Connection failed with UnknownHttpStatusCodeException"));
    }
}