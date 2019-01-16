package uk.gov.hmcts.probate.services.submit.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SubmitHealthConfiguration {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.persistence.baseUrl}")
    private String servicesPersistenceBaseUrl;

    @Value("${idam.s2s-auth.url}")
    private String idamS2sAuthUrl;

    @Bean
    public SubmitHealthIndicator persistenceServiceHealthIndicator() {
        return new SubmitHealthIndicator(servicesPersistenceBaseUrl, restTemplate);
    }

    @Bean
    public SubmitHealthIndicator serviceAuthHealthIndicator() {
        return new SubmitHealthIndicator(idamS2sAuthUrl, restTemplate);
    }
}
