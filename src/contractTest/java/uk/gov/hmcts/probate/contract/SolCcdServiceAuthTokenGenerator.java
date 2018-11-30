package uk.gov.hmcts.probate.contract;


import io.restassured.RestAssured;
import java.util.Base64;

import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

@Component
@Slf4j
public class SolCcdServiceAuthTokenGenerator {

    @Value("${idam.oauth2.client.id}")
    private String clientId;

    @Value("${idam.oauth2.client.secret}")
    private String clientSecret;

    @Value("${idam.oauth2.redirect_uri}")
    private String redirectUri;

    @Value("${service.name}")
    private String serviceName;

    @Value("${service.auth.provider.base.url}")
    private String baseServiceAuthUrl;

    String clientToken;

    @Value("${idam.username}")
    private String idamUsername;

    @Value("${idam.userpassword}")
    private String idamPassword;

    @Value("${env}")
    private String environment;

    @Value("${idam.secret}")
    private String secret;

    @Value("${user.auth.provider.oauth2.url}")
    private String idamUserBaseUrl;

    private String userToken;


    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    @Autowired
    private ServiceAuthTokenGenerator tokenGenerator;

    public String generateServiceToken() {
        String sAuth = tokenGenerator.generate();
        return sAuth;
    }


    public String getUserId() {
        String userid_local = "" + RestAssured.given()
                .header("Authorization", userToken)
                .get(idamUserBaseUrl + "/details")
                .body()
                .path("id");
        return userid_local;
    }


    public String generateUserTokenWithNoRoles() {
        userToken = generateClientToken();
        return userToken;
    }

    private String generateClientToken() {
        String code = generateClientCode();
        log.info("CODE="+code);
        String token = "";

        String path = idamUserBaseUrl + "/oauth2/token?code=" + code +
                "&client_secret=" + secret +
                "&client_id=probate" +
                "&redirect_uri=" + redirectUri +
                "&grant_type=authorization_code";
        log.info("PATH="+path);
        ResponseBody body = RestAssured.given().post(path)
                .body();
        log.info("BODY="+body);
        token = body.path("access_token");

        return "Bearer " + token;
    }

    private String generateClientCode() {
        String code = "";
        code = RestAssured.given().header("Authorization", "Basic dGVzdEBURVNULkNPTToxMjM=")
                .post(idamUserBaseUrl + "/oauth2/authorize?response_type=code" +
                        "&client_id=" + clientId +
                        "&redirect_uri=" + redirectUri).body().path("code");
        return code;

    }
}
