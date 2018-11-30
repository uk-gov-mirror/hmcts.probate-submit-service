package uk.gov.hmcts.probate.contract;


import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.util.Base64;

import static io.restassured.RestAssured.given;

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
        System.out.println("CODE=" + code);
        String token = "";

        String path = idamUserBaseUrl + "/oauth2/token?code=" + code +
                "&client_secret=" + secret +
                "&client_id=probate" +
                "&redirect_uri=" + redirectUri +
                "&grant_type=authorization_code";
        System.out.println("PATH=" + path);
        ResponseBody body = RestAssured.given().post(path)
                .body();
        System.out.println("BODY=" + body);
        token = body.path("access_token");

        return "Bearer " + token;
    }

    private String generateClientCode() {
        System.out.println("idamUsername=" + idamUsername);
        System.out.println("idamPassword=" + idamPassword);
        String code = "";
        final String encoded = Base64.getEncoder().encodeToString((idamUsername + ":" + idamPassword).getBytes());
        System.out.println("encoded=" + encoded);
        System.out.println("redirectUri=" + redirectUri.replaceAll("A", " A "));
        code = RestAssured.given().baseUri(idamUserBaseUrl)
                .header("Authorization", "Basic " + encoded)
                .post("/oauth2/authorize?response_type=code&client_id=probate&redirect_uri=" + redirectUri)
                .body().path("code");
        return code;

    }

    public void createNewUser() {
        given().headers("Content-type", "application/json")
                .relaxedHTTPSValidation()
                .body("{ \"email\":\"test@TEST.COM\", \"forename\":\"test@TEST.COM\",\"surname\":\"test@TEST.COM\",\"password\":\"123\",\"continue-url\":\"test\"}")
                .post(idamUserBaseUrl + "/testing-support/accounts");
    }
}
