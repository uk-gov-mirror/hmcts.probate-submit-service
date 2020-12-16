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
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.ObjectMapperTestUtil.convertObjectToJsonString;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;

public class ProbateSubmitServiceSubmitForCitizen extends AbstractProbateSubmitServicePact {

    public CaseDataContent caseDataContent;
    private static final String USER_ID = "123456";
    private static final String BASECASE_PAYLOAD_PATH = "json/GetCaseData.json";
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
    RequestResponsePact submitForCitizen(PactDslWithProvider builder) throws Exception {
        // @formatter:off
        return builder
                .given("A Submit for a Citizen is requested",setUpStateMapForProviderWithCaseData(EVENT_ID))
                .uponReceiving("A Submit for a Citizen is requested")
                .path("/citizens/"
                        + USER_ID
                        + "/jurisdictions/"
                        + jurisdictionId
                        + "/case-types/"
                        + caseType
                        + "/cases")
                .query("ignore-warning=true")
                .method("POST")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN,
                        SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
                .body(convertObjectToJsonString(getCaseDataContent(EVENT_ID,BASECASE_PAYLOAD_PATH)))
                .willRespondWith()
                .status(200)
                .body(buildCaseDetailsDsl(100L, true,true))
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "submitForCitizen")
    public void verifySubmitForCitizen() throws Exception {

        caseDataContent = getCaseDataContent(EVENT_ID,BASECASE_PAYLOAD_PATH);

        final CaseDetails caseDetails = coreCaseDataApi.submitForCitizen(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN,
                USER_ID, jurisdictionId,caseType,
                true, caseDataContent);

        assertThat(caseDetails.getCaseTypeId() , is("GrantOfRepresentation"));
        assertThat(caseDetails.getJurisdiction() , is("PROBATE"));
        assertThat(caseDetails.getState(), is(notNullValue()));

        assertCaseDetails(caseDetails, false , false);

    }

    @Override
    protected Map<String, Object> setUpStateMapForProviderWithCaseData(String eventId) throws Exception {
        Map<String, Object> caseDataContentMap = super.setUpStateMapForProviderWithCaseData(eventId);
        caseDataContentMap.put(EVENT_ID, PAYMENT_SUCCESS_APP);
        return caseDataContentMap;
    }

}
