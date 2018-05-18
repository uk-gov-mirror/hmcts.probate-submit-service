package uk.gov.hmcts.probate.services.submit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Configuration
@ConfigurationProperties
public class ApplicationConfig {
    private List<Registry> registries = new ArrayList<>();

    @Autowired
    public List<Registry> getRegistries() {
        return registries;
    }

    @Bean
    public Map<Integer, Registry> registryMap() {
        return registries
                .stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s));
    }
}
