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
@EnableWebSecurity
public class SecurityConfiguration {

    private final ServiceAuthFilter serviceAuthFilter;

    public SecurityConfiguration(ServiceAuthFilter serviceAuthFilter) {
        this.serviceAuthFilter = serviceAuthFilter;
    }

    @Bean
    protected SecurityFilterChain allowedList(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/swagger-ui.html",
                    "/swagger-resources/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/health",
                    "/health/liveness",
                    "/info",
                    "/").permitAll()
                .anyRequest().authenticated())
            .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .addFilterBefore(serviceAuthFilter, BearerTokenAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/cases/**",
                    "/submissions/**",
                    "/payments/**",
                    "/ccd-case-update/**").authenticated())
            .build();
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
