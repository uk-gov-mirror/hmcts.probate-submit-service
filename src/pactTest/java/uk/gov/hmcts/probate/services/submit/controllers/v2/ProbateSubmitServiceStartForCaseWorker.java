package uk.gov.hmcts.probate.services.submit.controllers.v2;

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
import uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.AbstractProbateSubmitServicePact;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventReponse;

public class ProbateSubmitServiceStartForCaseWorker extends AbstractProbateSubmitServicePact {

    private static final String USER_ID = "123456";

    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @After
    void teardown() {
        Executor.closeIdleConnections();
    }


    @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_submitService")
    public RequestResponsePact startForCaseWorker(PactDslWithProvider builder) throws Exception {
        // @formatter:off
        return builder
                .given("A Start for caseworker is received",setUpStateMapForProvider(EVENT_ID))
                .uponReceiving("A Start a caseworker is requested")
                .path("/caseworkers/" + USER_ID + "/jurisdictions/"
                        + jurisdictionId + "/case-types/"
                        + caseType
                        + "/event-triggers/"
                        + createEventId
                        + "/token")
                .method("GET")
                .headers(HttpHeaders.AUTHORIZATION,
                        SOME_AUTHORIZATION_TOKEN,
                        SERVICE_AUTHORIZATION,
                        SOME_SERVICE_AUTHORIZATION_TOKEN)
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .status(200)
                .body(buildStartEventReponse(createEventId , "token",true,
                        true))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "startForCaseWorker")
    public void verifyStartForCaseworker() throws Exception {

        final StartEventResponse startEventResponse = coreCaseDataApi.startForCaseworker(
                SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN,
                USER_ID, jurisdictionId,
                caseType, createEventId);

        assertThat(startEventResponse.getEventId() , notNullValue());

        assertCaseDetails(startEventResponse.getCaseDetails(), true , true);

    }

    @Override
    protected Map<String, Object> setUpStateMapForProvider(String eventId) throws Exception {
        Map<String, Object> caseDataContentMap = super.setUpStateMapForProvider(eventId);
        caseDataContentMap.put(EVENT_ID, APPLY_FOR_GRANT);
        return caseDataContentMap;
    }

}


