package uk.gov.hmcts.probate.functional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
@RunWith(SerenityRunner.class)
public class IntestacyGrantOfRepresentationTests extends IntegrationTestBase {

    private static final String PASSWORD = "Probate123";
    private static final String USER_GROUP_NAME = "probate-private-beta";

    private ObjectMapper objectMapper = new ObjectMapper();

    private String email;

    @Before
    public void setUp() throws JsonProcessingException {
        RestAssured.defaultParser = Parser.JSON;
        String forename = RandomStringUtils.randomAlphanumeric(5);
        String surname = RandomStringUtils.randomAlphanumeric(5);
        email = forename + "." + surname + "@email.com";

        System.out.println("***************** " + email + " *******************");

        IdamData idamData = IdamData.builder()
                .email(email)
                .forename(forename)
                .surname(surname)
                .password(PASSWORD)
                .userGroupName(USER_GROUP_NAME)
                .build();

        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(Headers.headers(new Header("Content-Type", ContentType.JSON.toString())))
                .baseUri(idamUrl)
                .body(objectMapper.writeValueAsString(idamData))
                .when()
                .post("/testing-support/accounts")
                .then()
                .statusCode(204);

    }

    @Test
    public void testA_shouldSaveDraftSuccessfully(){

        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders(email, PASSWORD))
                .body(utils.getJsonFromFile("intestacyGrantOfRepresentation.json"))
                .when()
                .post(submitServiceUrl + "/drafts/" + email)
                .then()
                .assertThat()
                .statusCode(200);
    }
}
