package uk.gov.hmcts.probate.services.submit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Configuration
@PropertySource(value = "git.properties", ignoreResourceNotFound = true)
public class RegistryConfig {

    private List<Registry> registries = new ArrayList<>();

    @Autowired
    public List<Registry> getRegistries() {
        return registries;
    }

    @Bean
    public Map<Long, Registry> registryMap() {
        return registries
                .stream()
                .collect(Collectors.toMap(Registry::getId, s -> s));
    }
}
