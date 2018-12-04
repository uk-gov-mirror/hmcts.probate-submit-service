package uk.gov.hmcts.probate.contract.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.parsing.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.contract.SolCcdServiceAuthTokenGenerator;
import uk.gov.hmcts.probate.contract.TestContextConfiguration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;


@ContextConfiguration(classes = TestContextConfiguration.class)
@Component
public class ContractTestUtils {

    @Autowired
    protected SolCcdServiceAuthTokenGenerator solCcdServiceAuthTokenGenerator;

    private ObjectMapper objectMapper;

    private String serviceToken;

    private String userId;

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String CONTENT_TYPE = "Content-Type";

    @PostConstruct
    public void init() {
        serviceToken = solCcdServiceAuthTokenGenerator.generateServiceToken();
        RestAssured.defaultParser = Parser.JSON;
        System.out.println("Service Token: " + serviceToken);
        objectMapper = new ObjectMapper();

        System.out.println("userId="+userId);
        if (userId == null || userId.isEmpty()) {
            solCcdServiceAuthTokenGenerator.createNewUser();
            userId = solCcdServiceAuthTokenGenerator.getUserId();
        }
    }

    public JsonNode getJsonNodeFromFile(String fileName) throws IOException {
        File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
        return objectMapper.readTree(file);
    }

    public Headers getHeaders() {
        return getHeaders(serviceToken);
    }

    public Headers getHeaders(String serviceToken) {
        return Headers.headers(
                new Header(SERVICE_AUTHORIZATION, serviceToken),
                new Header(CONTENT_TYPE, ContentType.JSON.toString()));
    }

    public Headers getHeadersWithUserId() {
        return getHeadersWithUserId(serviceToken, userId);
    }

    private Headers getHeadersWithUserId(String serviceToken, String userId) {
        System.out.println("getHeadersWithUserId.serviceToken="+serviceToken);
        System.out.println("getHeadersWithUserId.userId="+userId);
        return Headers.headers(
                new Header(SERVICE_AUTHORIZATION, serviceToken),
                new Header(CONTENT_TYPE, ContentType.JSON.toString()),
                new Header("user-id", userId));
    }

    public String getUserId() {
        return userId;
    }
}
