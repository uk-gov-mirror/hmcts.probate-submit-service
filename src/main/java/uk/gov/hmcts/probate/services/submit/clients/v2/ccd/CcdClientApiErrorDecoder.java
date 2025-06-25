package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;
import uk.gov.hmcts.reform.probate.model.client.ErrorResponse;

@Slf4j
@RequiredArgsConstructor
public class CcdClientApiErrorDecoder implements ErrorDecoder {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Response status: {} - {}", response.status(), response.reason());

        ResponseDecorator responseDecorator = new ResponseDecorator(response, objectMapper);
        ErrorResponse clientErrorResponse = responseDecorator.mapBodyToErrorResponse();

        return new ApiClientException(response.status(), clientErrorResponse);
    }
}
