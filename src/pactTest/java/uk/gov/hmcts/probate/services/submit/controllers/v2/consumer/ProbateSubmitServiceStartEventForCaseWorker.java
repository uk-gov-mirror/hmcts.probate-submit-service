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
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventReponse;

public class ProbateSubmitServiceStartEventForCaseWorker  extends AbstractProbateSubmitServicePact {

    private static final String USER_ID = "123456";
    private static final String CASE_ID = "2000";
    public static final String EVENT_ID = "eventId";


    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @After
    void teardown() {
        Executor.closeIdleConnections();
    }


    @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_submitService")
    RequestResponsePact startEventForCaseWorker(PactDslWithProvider builder) throws Exception {
        // @formatter:off
        return builder
                .given("A Start Event for a Caseworker is  requested", setUpStateMapForProvider(EVENT_ID))
                .uponReceiving("A StartEvent for caseworker is requested")
                .path("/caseworkers/" + USER_ID + "/jurisdictions/"
                        + jurisdictionId + "/case-types/"
                        + caseType
                        + "/cases/" + CASE_ID
                        + "/event-triggers/"
                        + EVENT_ID
                        + "/token")
                .method("GET")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                        SOME_SERVICE_AUTHORIZATION_TOKEN)
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .status(200)
                .body(buildStartEventReponse(EVENT_ID , "token",true,
                        true))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "startEventForCaseWorker")
    public void verifyStartEventForCaseworker() throws Exception {

        final StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
                caseType, CASE_ID, EVENT_ID);

        assertThat(startEventResponse.getEventId() , notNullValue());
        assertCaseDetails(startEventResponse.getCaseDetails(), true , true);

    }

    @Override
    protected Map<String, Object> setUpStateMapForProviderWithCaseData(String eventId) throws Exception {
        Map<String, Object> caseDataContentMap = super.setUpStateMapForProvider(eventId);
        caseDataContentMap.put(EVENT_ID, APPLY_FOR_GRANT);
        return caseDataContentMap;
    }
}
