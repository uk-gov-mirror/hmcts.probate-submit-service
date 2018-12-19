package uk.gov.hmcts.probate.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.gov.hmcts.reform.auth.checker.core.RequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.AuthCheckerServiceAndUserFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private AuthCheckerServiceAndUserFilter authCheckerServiceAndUserFilter;

    private AuthenticationExceptionHandler authenticationExceptionHandler;

    @Autowired
    public SecurityConfiguration(RequestAuthorizer<User> userRequestAuthorizer,
                                 RequestAuthorizer<Service> serviceRequestAuthorizer,
                                 AuthenticationManager authenticationManager,
                                 AuthenticationExceptionHandler authenticationExceptionHandler) {
        authCheckerServiceAndUserFilter = new AuthCheckerServiceAndUserFilter(serviceRequestAuthorizer, userRequestAuthorizer);
        authCheckerServiceAndUserFilter.setAuthenticationManager(authenticationManager);
        this.authenticationExceptionHandler = authenticationExceptionHandler;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/swagger-ui.html",
                "/webjars/springfox-swagger-ui/**",
                "/swagger-resources/**",
                "/v2/**",
                "/health",
                "/info",
                "/favicon.ico",
                "/submit",
                "/updatePaymentStatus",
                "/resubmit/**",
                "/")
                .antMatchers(HttpMethod.POST, "/submit")
                .antMatchers(HttpMethod.POST, "/updatePaymentStatus");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(authCheckerServiceAndUserFilter)
                .sessionManagement().sessionCreationPolicy(STATELESS).and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/submit").permitAll()
                .antMatchers(HttpMethod.POST, "/updatePaymentStatus").permitAll()
                .antMatchers("/resubmit/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/webjars/springfox-swagger-ui/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationExceptionHandler);
    }
}
