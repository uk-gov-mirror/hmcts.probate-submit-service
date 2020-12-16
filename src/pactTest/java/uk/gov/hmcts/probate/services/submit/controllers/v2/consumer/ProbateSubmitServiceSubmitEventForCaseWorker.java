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
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util.ObjectMapperTestUtil.convertObjectToJsonString;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;

public class ProbateSubmitServiceSubmitEventForCaseWorker extends AbstractProbateSubmitServicePact {

  private static final String USER_ID = "123456";
  private static final String CASE_ID = "2000";
  private static final String BASECASE_PAYLOAD_PATH = "json/GetCaseData.json";
  public static final String EVENT_ID = "eventId";

  @BeforeEach
  public void setUpEachTest() throws Exception {
    Thread.sleep(2000);
    caseDataContent = getCaseDataContent(PAYMENT_SUCCESS_APP,BASECASE_PAYLOAD_PATH);
  }

  @After
  void teardown() {
    Executor.closeIdleConnections();
  }


  @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_submitService")
  RequestResponsePact submitEventForCaseWorker(PactDslWithProvider builder) throws Exception {
    // @formatter:off
    return builder
            .given("A SubmitEvent for Caseworker is triggered",setUpStateMapForProvider(EVENT_ID))
            .uponReceiving("A SubmitEvent For Caseworker is received.")
            .path("/caseworkers/"   + USER_ID
                    + "/jurisdictions/" + jurisdictionId
                    + "/case-types/"    + caseType
                    + "/cases/"         + CASE_ID
                    + "/events"
            )
            .query("ignore-warning=true")
            .method("POST")
            .headers(HttpHeaders.AUTHORIZATION,
                    SOME_AUTHORIZATION_TOKEN,
                    SERVICE_AUTHORIZATION,
                    SOME_SERVICE_AUTHORIZATION_TOKEN)
            .body(convertObjectToJsonString(getCaseDataContent(EVENT_ID,BASECASE_PAYLOAD_PATH)) )
            .willRespondWith()
            .status(200)
            .body(buildCaseDetailsDsl(Long.valueOf(CASE_ID),true,true))
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "submitEventForCaseWorker")
  public void verifySubmitEventForCaseworker() throws Exception {

    caseDataContent = getCaseDataContent(EVENT_ID,BASECASE_PAYLOAD_PATH);

    final CaseDetails caseDetails = coreCaseDataApi.submitEventForCaseWorker(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN,
            USER_ID, jurisdictionId, caseType,
            CASE_ID,true,caseDataContent);

    assertThat(caseDetails.getCaseTypeId() , is("GrantOfRepresentation"));
    assertThat(caseDetails.getJurisdiction() , is("PROBATE"));
    assertThat(caseDetails.getState(), is(notNullValue()));

    assertCaseDetails(caseDetails, true , true);

  }

  @Override
  protected Map<String, Object> setUpStateMapForProvider(String eventId) throws Exception {
    Map<String, Object> caseDataContentMap = super.setUpStateMapForProvider(eventId);
    caseDataContentMap.put(EVENT_ID, APPLY_FOR_GRANT);
    return caseDataContentMap;
  }
}