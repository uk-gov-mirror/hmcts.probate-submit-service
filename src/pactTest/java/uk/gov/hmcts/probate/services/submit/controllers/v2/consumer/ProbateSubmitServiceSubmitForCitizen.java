package uk.gov.hmcts.probate.services.submit.controllers.v2.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.client.fluent.Executor;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.ObjectMapperTestUtil.convertObjectToJsonString;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.PactDslFixtureHelper.getCaseDataContent;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;

public class ProbateSubmitServiceSubmitForCitizen extends AbstractProbateSubmitServicePact {

    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";

    @Autowired
    private CoreCaseDataApi coreCaseDataApi;

    @Value("${ccd.jurisdictionid}")
    String jurisdictionId;

    @Value("${ccd.casetype}")
    String caseType;

    CaseDataContent caseDataContent;

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


    @Pact(provider = "ccd", consumer = "probate_service")
    RequestResponsePact submitForCitizen(PactDslWithProvider builder) throws Exception {
        // @formatter:off
        return builder
                .given("Submit for citizen is triggered")
                .uponReceiving("A Submit For Citizen")
                .path("/citizens/"
                        + USER_ID
                        + "/jurisdictions/"
                        + jurisdictionId
                        + "/case-types/"
                        + caseType
                        + "/cases")
                .query("ignore-warning=true")
                .method("POST")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
                .body(convertObjectToJsonString(getCaseDataContent()))
                .willRespondWith()
                .status(200)
                .body(buildCaseDetailsDsl(100L, "someemailaddress.com", false, false,false))
                .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "submitForCitizen")
    public void verifySubmitForCitizen() throws Exception {

        final CaseDetails caseDetails = coreCaseDataApi.submitForCitizen(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,caseType,true, caseDataContent);

        assertThat(caseDetails.getCaseTypeId() , is("GrantOfRepresentation"));
        assertThat(caseDetails.getJurisdiction() , is("PROBATE"));
        assertThat(caseDetails.getState(), is(notNullValue()));

        assertCaseDetails(caseDetails, false , false);

    }

}
