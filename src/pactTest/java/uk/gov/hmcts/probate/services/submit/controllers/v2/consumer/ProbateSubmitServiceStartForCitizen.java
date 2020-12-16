package uk.gov.hmcts.probate.services.submit.controllers.v2.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.client.fluent.Executor;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventReponse;

public class ProbateSubmitServiceStartForCitizen extends AbstractProbateSubmitServicePact{

    private static final String USER_ID = "123456";
    public static final String EVENT_ID = "eventId";
    private static final String BASECASE_PAYLOAD_PATH = "json/BaseStartJson.json";

    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @After
    void teardown() {
        Executor.closeIdleConnections();
    }


    @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_submitService")
    RequestResponsePact startForCitizen(PactDslWithProvider builder) throws Exception {
        return builder
                .given("A Start for a Citizen is requested",setUpStateMapForProvider(EVENT_ID))
                .uponReceiving("A Start a citizen is requested")
                .path("/citizens/" + USER_ID + "/jurisdictions/"
                        + jurisdictionId + "/case-types/"
                        + caseType
                        + "/event-triggers/"
                        + createEventId
                        + "/token")
                .method("GET")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                        SOME_SERVICE_AUTHORIZATION_TOKEN)
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .status(200)
                .body(buildStartEventReponse(createEventId , "token",true,
                        true))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "startForCitizen")
    public void verifyStartEventForCitizen() throws Exception {

        caseDataContent = getCaseDataContent(EVENT_ID,BASECASE_PAYLOAD_PATH);

        final StartEventResponse startEventResponse = coreCaseDataApi.startForCitizen(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
                caseType, createEventId);

        assertThat(startEventResponse.getEventId() , notNullValue());
        assertCaseDetails(startEventResponse.getCaseDetails(), true , true);

    }

    @Override
    protected Map<String, Object> setUpStateMapForProvider(String eventId) throws Exception {
        Map<String, Object> caseDataContentMap = super.setUpStateMapForProvider(eventId);
        caseDataContentMap.put(eventId, APPLY_FOR_GRANT);
        return caseDataContentMap;
    }
}
