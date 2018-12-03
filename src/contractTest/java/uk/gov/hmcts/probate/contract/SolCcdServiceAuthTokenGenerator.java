package uk.gov.hmcts.probate.contract;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.contract.model.ClientAuthorizationCodeResponse;
import uk.gov.hmcts.probate.contract.model.ClientAuthorizationResponse;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.io.IOException;
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

    private final int rnd = (int) (Math.random() * 1000000);
    private final String pass = "123";


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
        String clientToken = generateClientToken();

        String withoutSignature = clientToken.substring(0, clientToken.lastIndexOf('.') + 1);
        Claims claims = Jwts.parser().parseClaimsJwt(withoutSignature).getBody();

        return claims.get("id", String.class);
    }


    private String generateClientToken() {
        String code = generateClientCode();
        System.out.println("CODE=" + code);
        String token = "";

        String path = idamUserBaseUrl + "/oauth2/token?code=" + code +
                "&client_secret=" + secret +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&grant_type=authorization_code";
        System.out.println("PATH=" + path);
        ResponseBody body = RestAssured.given().post(path)
                .body();
        System.out.println("BODY=" + body.prettyPrint());
        String jsonResponse = body.asString();

        ObjectMapper mapper = new ObjectMapper();

        try {
            token = mapper.readValue(jsonResponse, ClientAuthorizationResponse.class).accessToken;
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return token;
    }

    private String generateClientCode() {
        String code = "";
        final String encoded = Base64.getEncoder().encodeToString(("test123@TEST.COM:Password123").getBytes());

        System.out.println("encoded="+encoded);
        String jsonResponse = given()
                .relaxedHTTPSValidation()
                .header("Authorization", "Basic "+encoded)
                .post(idamUserBaseUrl + "/oauth2/authorize?response_type=code" +
                        "&client_id=" + clientId +
                        "&redirect_uri=" + redirectUri)
                .asString();

        System.out.println("jsonResponse="+jsonResponse);
        ObjectMapper mapper = new ObjectMapper();

        try {
            code = mapper.readValue(jsonResponse, ClientAuthorizationCodeResponse.class).code;
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return code;

    }

    public void createNewUser() {
        given().headers("Content-type", "application/json")
                .relaxedHTTPSValidation()
                .body("{ \"email\":\"test123@TEST.COM\", \"forename\":\"test123@TEST.COM\",\"surname\":\"test123@TEST.COM\",\"password\":\"Password123\",\"continue-url\":\"test\"}")
                .post(idamUserBaseUrl + "/testing-support/accounts");
    }
}
