package uk.gov.hmcts.probate.services.submit.clients;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class CoreCaseDataClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${services.coreCaseData.url}")
    private String coreCaseDataServiceURL;

    private RestTemplate restTemplate;
    private PersistenceEntityBuilder builder;
    private CoreCaseDataMapper ccdMapper;
    
    private String ccdEventId = "applyForGrant";

    @Autowired
    public CoreCaseDataClient(RestTemplate restTemplate, PersistenceEntityBuilder builder, CoreCaseDataMapper ccdMapper) {
        this.restTemplate = restTemplate;
        this.builder = builder;
        this.ccdMapper = ccdMapper;
    }

    @Retryable(backoff = @Backoff(delay = 100, maxDelay = 500))
    public JsonNode createCase(String userId, String authorization) {
        String startUrl = String.format(coreCaseDataServiceURL, userId) + "/event-triggers/" + ccdEventId + "/token";
        logger.info("Start case " + startUrl);
        HttpEntity<JsonNode> request = builder.createCcdStartRequest(authorization);
        ResponseEntity<JsonNode> response = restTemplate.exchange(startUrl, HttpMethod.GET, request, JsonNode.class); 
        logger.info("Status code:" + response.getStatusCodeValue());
        logger.info("Response body:" + response.toString());
        return response.getBody();
    }
                                                                                                                                                                                                                                                                                                                               
    @Retryable(backoff = @Backoff(delay = 100, maxDelay = 500))
    public void saveCase(JsonNode submitData, String userId, String authorization, JsonNode ccdStartCaseResponse, Calendar submissonTimestamp, JsonNode sequenceNumber) {      
        JsonNode ccdToken = ccdStartCaseResponse.get("token");
        JsonNode ccdData = ccdMapper.createCcdData(submitData, ccdEventId, ccdToken, submissonTimestamp, sequenceNumber);
        HttpEntity<JsonNode> persistenceRequest = builder.createCcdSaveRequest(ccdData, authorization);
        String saveUrl = String.format(coreCaseDataServiceURL, userId) + "/cases";        
        logger.info("Save case " + saveUrl);
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(saveUrl, HttpMethod.POST, persistenceRequest, JsonNode.class);
            logger.info("Status: " + response.getStatusCodeValue());
            logger.info("Response body:" + response.toString());
        } catch (HttpClientErrorException e) {
            logger.info("Exception while saving case", e);
        }
    }
}
