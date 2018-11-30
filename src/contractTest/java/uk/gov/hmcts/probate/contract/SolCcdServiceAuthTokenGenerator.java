package uk.gov.hmcts.probate.contract;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
        token = body.path("access_token");

        return "Bearer " + token;
    }

    private String generateClientCode() {
        System.out.println("idamUsername=" + idamUsername);
        System.out.println("idamPassword=" + idamPassword);
        String code = "";
        String user = "test" + rnd + "@TEST.COM";
        final String encoded = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
        System.out.println("encoded=" + encoded);
        System.out.println("redirectUri=" + redirectUri);
        code = RestAssured.given().baseUri(idamUserBaseUrl)
                .header("Authorization", "Basic " + encoded)
                .post("/oauth2/authorize?response_type=code&client_id="+clientId+"&redirect_uri=" + redirectUri)
                .body().path("code");
        return code;

    }

    public void createNewUser() {
        String user = "test" + rnd + "@TEST.COM";
        System.out.println("user="+user+ " pass="+pass);
        given().headers("Content-type", "application/json")
                .relaxedHTTPSValidation()
                .body("{ \"email\":\"\"+user+\"\", \"forename\":\""+user+"\",\"surname\":\"\"+user+\"\",\"password\":\""+pass+"\",\"continue-url\":\"test\"}")
                .post(idamUserBaseUrl + "/testing-support/accounts");
    }
}
