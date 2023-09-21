package uk.gov.hmcts.probate.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;

@Profile("!SECURITY_MOCK")
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfiguration {

    private final ServiceAuthFilter serviceAuthFilter;

    public SecurityConfiguration(ServiceAuthFilter serviceAuthFilter) {
        this.serviceAuthFilter = serviceAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/swagger-ui.html",
                    "/swagger-resources/**",
                    "/swagger-ui/**",
                    "/health",
                    "/health/liveness",
                    "/info",
                    "/").permitAll()
                .requestMatchers(
                    "/cases/**",
                    "/submissions/**",
                    "/payments/**",
                    "/ccd-case-update/**").authenticated())
            .addFilterBefore(serviceAuthFilter, BearerTokenAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/swagger-ui.html",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/health",
                "/health/liveness",
                "/info",
                "/");
    }
}
