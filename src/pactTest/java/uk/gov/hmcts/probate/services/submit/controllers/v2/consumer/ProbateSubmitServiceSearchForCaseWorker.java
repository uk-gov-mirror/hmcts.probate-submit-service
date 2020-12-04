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
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildNewListOfCaseDetailsDsl;

public class ProbateSubmitServiceSearchForCaseWorker extends AbstractProbateSubmitServicePact {
    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final String USER_ID = "123456";
    private static final String CASE_ID = "2000";
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @After
    void teardown() {
        Executor.closeIdleConnections();
    }


    @Pact(provider = "ccd", consumer = "probate_search_caseworker")
    RequestResponsePact searchForCaseWorker(PactDslWithProvider builder) throws Exception {
        // @formatter:off
        return builder
                .given("A StartEvent for caseworkers is received")
                .uponReceiving("A StartEvent for caseworker is requested")
                .path("/caseworkers/"
                        + USER_ID
                        + "/jurisdictions/"
                        + jurisdictionId
                        + "/case-types/"
                        + caseType
                        + "/cases")
                .query("ignore-warning=true")
                .method("GET")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                        SOME_SERVICE_AUTHORIZATION_TOKEN)
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
                .status(200)
                .body(buildNewListOfCaseDetailsDsl(100L, "someemailaddress.com", false, false,false))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "searchForCaseWorker")
    public void verifySearchForCaseworker() throws Exception {

        final Map<String, String> searchCriteria = Collections.EMPTY_MAP;

        List<CaseDetails> caseDetailsList = coreCaseDataApi.searchForCaseworker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
                caseType, searchCriteria);

              assertNotNull(caseDetailsList);

    }
}
