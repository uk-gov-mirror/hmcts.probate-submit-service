package uk.gov.hmcts.probate.services.submit.controllers.v2.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.EventId;
import uk.gov.hmcts.reform.probate.model.cases.JurisdictionId;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "ccd", port = "8891")
@SpringBootTest({
    "core_case_data.api.url : localhost:8891"
})
public class CcdConsumerTest {

    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final String ACCESS_TOKEN = "someAccessToken";
    public static final String REGEX_DATE = "^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
    private static final String TOKEN = "someToken";

    @Autowired
    private CoreCaseDataApi coreCaseDataApi;
    @Autowired
    ObjectMapper objectMapper;

    private Long USER_ID = 123456L;
    private Long CASE_ID = 654321L;
    private String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    private String EXPERIMENTAL = "experimental=true";
    private CaseDataContent caseDataContent;
    private CaseDetails caseDetails;

    @BeforeAll
    public void setUp() throws IOException, JSONException {
        caseDetails = getCaseDetails("ccdCaseDetails.json");
        StartEventResponse startEventResponse = StartEventResponse.builder()
            .token(TOKEN)
            .caseDetails(caseDetails)
            .eventId(EventId.GOP_UPDATE_DRAFT.getName())
            .build();

        GrantOfRepresentationData grantOfRepresentationData = getGrantOfRepresentationData("success.pa.ccd.json");
        caseDataContent = createCaseDataContent(grantOfRepresentationData, EventId.GOP_UPDATE_DRAFT, startEventResponse);
    }

    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @After
    void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(state = "GrantOfRepresentation Case 654321 exists", provider = "ccd_coreCaseDataApi_casesController", consumer = "probate_submitService")
    RequestResponsePact getCaseById(PactDslWithProvider builder) {
        // @formatter:off
        return builder
            .given("A GrantOfRepresentation case exists")
            .uponReceiving("a request for that case")
            .path("/cases/" + CASE_ID)
            .method("GET")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
            .matchHeader("experimental", "true")
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
            .status(200)
            .body(CaseDataPactDslBuilder.build(CASE_ID, "someEmailAddress.com", true, true))
            .toPact();
    }


    @Pact(state = "Start event for citizen", provider = "ccd_coreCaseDataApi_casesController", consumer = "probate_submitService")
    RequestResponsePact startEventForCitizen(PactDslWithProvider builder) {

        // @formatter:off
        return builder
            .given("A start request for citizen is requested")
            .uponReceiving("a request for a valid start event")
            .path("/citizens/" + USER_ID + "/jurisdictions/"
                + JurisdictionId.PROBATE.name() + "/case-types/"
                + CaseType.GRANT_OF_REPRESENTATION.getName()
                + "/cases/" + CASE_ID
                + "/event-triggers/"
                + EventId.GOP_UPDATE_DRAFT.getName()
                + "/token")
            .method("GET")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                SOME_SERVICE_AUTHORIZATION_TOKEN)
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
            .status(200)
            .body(newJsonBody((o) -> {
                o.stringValue("event_id", EventId.GOP_UPDATE_DRAFT.name())
                    .stringType("token", "123234543456");
            }).build())
            .toPact();
    }

    @Pact(state = "Submit event for citizen", provider = "ccd_coreCaseDataApi_casesController", consumer = "probate_submitService")
    RequestResponsePact submitEventForCitizen(PactDslWithProvider builder) throws IOException, JSONException {
        // @formatter:off
        return builder
            .given("A submit request for citizen is requested")
            .uponReceiving("a request for a valid submit event")
            .path("/citizens/" + USER_ID + "/jurisdictions/"
                + JurisdictionId.PROBATE.name() + "/case-types/"
                + CaseType.GRANT_OF_REPRESENTATION.getName()
                + "/cases/" + CASE_ID
                + "/events")
            .method("POST")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN,
                SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
            .matchQuery("ignore-warning", Boolean.TRUE.toString())
            .body(createJsonObject(caseDataContent))
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
            .status(201)
            .body(createJsonObject(caseDetails))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getCaseById")
    public void verifyGetCaseByIdPact() throws IOException, JSONException {

        CaseDetails caseDetailsResponse = coreCaseDataApi.getCase(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN, CASE_ID.toString());
        assertThat(caseDetailsResponse.getId(), equalTo(CASE_ID));

    }

    @Test
    @PactTestFor(pactMethod = "startEventForCitizen")
    public void verifyStartEventForCitizen() throws IOException, JSONException {

        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCitizen(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID.toString(), JurisdictionId.PROBATE.name(),
            CaseType.GRANT_OF_REPRESENTATION.getName(), CASE_ID.toString(), EventId.GOP_UPDATE_DRAFT.getName());
        assertThat(startEventResponse.getEventId(), equalTo(EventId.GOP_UPDATE_DRAFT.name()));

    }

    @Test
    @PactTestFor(pactMethod = "submitEventForCitizen")
    public void verifySubmitEventForCitizen() throws IOException, JSONException {
        CaseDetails caseDetails = coreCaseDataApi.submitEventForCitizen(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID.toString(), JurisdictionId.PROBATE.name(),
            CaseType.GRANT_OF_REPRESENTATION.getName(), CASE_ID.toString(), Boolean.TRUE, caseDataContent);
        assertThat(caseDetails.getId(), equalTo(CASE_ID));

    }

    protected JSONObject createJsonObject(Object obj) throws JSONException, IOException {
        String json = objectMapper.writeValueAsString(obj);
        return new JSONObject(json);
    }

    protected CaseDetails getCaseDetails(String fileName) throws JSONException, IOException {
        File file = getFile(fileName);
        CaseDetails caseDetails = objectMapper.readValue(file, CaseDetails.class);
        return caseDetails;
    }


    protected GrantOfRepresentationData getGrantOfRepresentationData(String fileName) throws JSONException, IOException {
        File file = getFile(fileName);
        GrantOfRepresentationData grantOfRepresentationData = objectMapper.readValue(file, GrantOfRepresentationData.class);
        return grantOfRepresentationData;
    }

    private File getFile(String fileName) throws FileNotFoundException {
        return ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
    }

    private CaseDataContent createCaseDataContent(CaseData caseData, EventId eventId, StartEventResponse startEventResponse) {
        return CaseDataContent.builder()
            .event(createEvent(eventId))
            .eventToken(startEventResponse.getToken())
            .data(caseData)
            .build();
    }

    private Event createEvent(EventId eventId) {
        return Event.builder()
            .id(eventId.getName())
            .description("Probate application")
            .summary("Probate application")
            .build();
    }
}
