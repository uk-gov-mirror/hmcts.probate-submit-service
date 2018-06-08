package uk.gov.hmcts.probate;


import io.restassured.RestAssured;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

@Component
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

    @Value("${user.auth.provider.oauth2.url}")
    private String baseServiceOauth2Url;
    String clientToken;

    @Value("${idam.username}")
    private String idamUsername;

    @Value("${idam.password}")
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
        return tokenGenerator.generate();
    }

    public String getUserId() {
        String userid_local = RestAssured.given()
                .header("Authorization", userToken)
                .get(idamUserBaseUrl + "/details")
                .body()
                .path("id");
        System.out.println("userid generated.." + userid_local);
        return userid_local;
    }


//    private void createUserInIdam() {
//
////        idamUsername = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
////        idamPassword = "Test123456";
////        System.out.println("idam user create url.." +idamCreateUrl());
////        Response res = RestAssured.given()
////                .header("Content-Type", "application/json")
////                .body("{\"email\":\"" + idamUsername + "\", \"forename\":\"Test\",\"surname\":\"User\",\"password\":\"" + idamPassword + "\",}")
////                .post(idamCreateUrl());
//        idamUsername = "venu7jun1@mailinator.com";
//        idamPassword = "Monday01";
//
//        //System.out.println("user created status..." + res.getStatusCode());
//    }


    public String generateUserTokenWithNoRoles() {
        // createUserInIdam();
        userToken = generateClientToken();
        System.out.println("token generated.." + userToken);
        return userToken;
    }


    //keeping this code to see if we need this mechanism after sidam integration.
    private String generateClientToken() {
        String code = generateClientCode();
        String token = "";

        token = RestAssured.given().post(idamUserBaseUrl + "/oauth2/token?code=" + code +
                "&client_secret=" + secret +
                "&client_id=probate" +
                "&redirect_uri=" + redirectUri +
                "&grant_type=authorization_code")
                .body().path("access_token");

        return "Bearer " + token;
    }

    private String generateClientCode() {
        String code = "";
        //  createUserInIdam();
        final String encoded = Base64.getEncoder().encodeToString((idamUsername + ":" + idamPassword).getBytes());
        code = RestAssured.given().baseUri(idamUserBaseUrl)
                .header("Authorization", "Basic " + encoded)
                .post("/oauth2/authorize?response_type=code&client_id=probate&redirect_uri=" + redirectUri)
                .body().path("code");
        return code;

    }
}