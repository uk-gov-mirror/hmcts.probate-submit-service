package uk.gov.hmcts.probate.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import uk.gov.hmcts.auth.provider.service.token.ServiceTokenGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final ServiceTokenGenerator serviceTokenGenerator;

    @Autowired
    public SecurityUtils(@Qualifier("cachedServiceTokenGenerator") final ServiceTokenGenerator serviceTokenGenerator) {
        this.serviceTokenGenerator = serviceTokenGenerator;
    }

    public HttpHeaders authorizationHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("ServiceAuthorization", serviceTokenGenerator.generate());
        return headers;
    }
}
