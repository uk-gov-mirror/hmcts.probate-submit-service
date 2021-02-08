package uk.gov.hmcts.probate.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.gov.hmcts.reform.auth.checker.core.RequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.AuthCheckerServiceAndUserFilter;

@Profile("!SECURITY_MOCK")
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private AuthCheckerServiceAndUserFilter filter;

    public SecurityConfiguration(RequestAuthorizer<Service> serviceRequestAuthorizer,
                                 AuthenticationManager authenticationManager,
                                 RequestAuthorizer<User> userRequestAuthorizer) {
        filter = new AuthCheckerServiceAndUserFilter(serviceRequestAuthorizer, userRequestAuthorizer);
        filter.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers()
                .antMatchers("/cases/**")
                .antMatchers("/submissions/**")
                .antMatchers("/payments/**")
                .antMatchers("/ccd-case-update/**")
                .antMatchers("/health", "/health/liveness")
                .and()
                .addFilter(filter)
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/springfox-swagger-ui/**",
            "/health",
            "/health/liveness",
            "/info",
            "/");
    }

}
