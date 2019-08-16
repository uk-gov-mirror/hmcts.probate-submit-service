package uk.gov.hmcts.probate.services.submit.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.submit.model.v2.Registry;

import java.util.List;

@Component
@Configuration
@ConfigurationProperties
@Data
public class RegistryConfig {

    private List<Registry> registries;

    @Bean
    public RegistryService registryService() {
        return new RegistryService(registries);
    }
}
