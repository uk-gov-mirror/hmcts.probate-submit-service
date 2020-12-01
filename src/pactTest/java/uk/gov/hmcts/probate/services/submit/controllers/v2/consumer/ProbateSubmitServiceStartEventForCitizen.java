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

import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.PactDslFixtureHelper.getCaseDataContent;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventReponse;


public class ProbateSubmitServiceStartEventForCitizen extends AbstractProbateSubmitServicePact{

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


  @Pact(provider = "ccd", consumer = "probate_service_citizen")
  RequestResponsePact startEventForCitizen(PactDslWithProvider builder) throws Exception {
    // @formatter:off
    return builder
            .given("A StartEvent for citizen is received")
            .uponReceiving("A StartEvent a citizen is requested")
            .path("/citizens/" + USER_ID + "/jurisdictions/"
                    + jurisdictionId + "/case-types/"
                    + caseType
                    + "/cases/" + CASE_ID
                    + "/event-triggers/"
                    + createEventId
                    + "/token")
            .method("GET")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                    SOME_SERVICE_AUTHORIZATION_TOKEN)
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
            .status(200)
            .body(buildStartEventReponse(createEventId , "token","someemailaddress.com",
                    false,false,false))
            .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "startEventForCitizen")
  public void verifyStartEventForCitizen() throws Exception {

    caseDataContent = getCaseDataContent();

    final StartEventResponse startEventResponse = coreCaseDataApi.startEventForCitizen(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
            caseType, CASE_ID.toString(), createEventId);

    assertThat(startEventResponse.getEventId() , notNullValue());
    assertThat(startEventResponse.getToken() , notNullValue());

    assertCaseDetails(startEventResponse.getCaseDetails(), false , false);

  }
}