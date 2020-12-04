package uk.gov.hmcts.probate.services.submit.controllers.v2.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.client.fluent.Executor;
import org.junit.After;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.AssertionHelper.assertListOfCaseDetails;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildListOfCaseDetailsDsl;

public class ProbateSubmitServiceSearchForCaseWorker extends AbstractProbateSubmitServicePact {
    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final String USER_ID = "123456";
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
                .method("GET")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                        SOME_SERVICE_AUTHORIZATION_TOKEN)
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
                .status(200)
                .body(buildListOfCaseDetailsDsl(100L))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "searchForCaseWorker")
    public void verifySearchForCaseworker() throws Exception {

        final Map<String, String> searchCriteria = Collections.EMPTY_MAP;

        List<CaseDetails> caseDetailsList = coreCaseDataApi.searchForCaseworker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId, caseType, searchCriteria);
              assertNotNull(caseDetailsList);

        Assert.assertTrue(isNotEmpty(caseDetailsList));
        assertListOfCaseDetails(caseDetailsList);


    }
}