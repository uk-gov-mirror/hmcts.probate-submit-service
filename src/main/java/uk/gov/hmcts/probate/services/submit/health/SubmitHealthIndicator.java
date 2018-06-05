package uk.gov.hmcts.probate.services.submit.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SubmitHealthIndicator implements HealthIndicator {
	
	private final static String ERROR_KEY = "error";

    private final String url;
    private RestTemplate restTemplate;
    
    @Override
    public Health health() {
    	ResponseEntity<String> responseEntity;

        try {
        	log.debug("Attempting to access health endpoint: " + url + "/health");
            responseEntity = restTemplate.getForEntity(url + "/health", String.class);
        } catch (ResourceAccessException rae) {
            log.error(rae.getMessage(), rae);
            return getHealthWithDownStatus("Connection failed with ResourceAccessException");
        } catch (HttpStatusCodeException hsce) {
            log.error(hsce.getMessage(), hsce);
            return getHealthWithDownStatus("HTTP Status: " + hsce.getStatusCode().value());
        } catch (UnknownHttpStatusCodeException uhsce) {
            log.error(uhsce.getMessage(), uhsce);
            return getHealthWithDownStatus("Connection failed with UnknownHttpStatusCodeException");
        }

        if (responseEntity != null && !responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return getHealthWithDownStatus("HTTP Status: " + responseEntity.getStatusCodeValue());
        }

        return getHealthWithUpStatus();

    }

    private Health getHealthWithUpStatus() {
        return Health.up()
                .build();
    }

    private Health getHealthWithDownStatus(String error) {
        return Health.down()
                .withDetail(ERROR_KEY, error)
                .build();
    }
}
