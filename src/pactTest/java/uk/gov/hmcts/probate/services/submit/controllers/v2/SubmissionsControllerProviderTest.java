package uk.gov.hmcts.probate.services.submit.controllers.v2;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import org.json.JSONException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.submit.services.SubmissionsService;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Provider("probate_submitservice_submissions")
@RunWith(SpringRestPactRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "server.port=8125", "spring.application.name=PACT_TEST"
})
@PactBroker(host = "${pact.broker.baseUrl}", port = "${pact.broker.port}")
public class SubmissionsControllerProviderTest extends ControllerProviderTest {

    @TestTarget
    @SuppressWarnings(value = "VisibilityModifier")
    public final Target target = new HttpTarget("http", "localhost", 8125, "/");

    @MockBean
    private SubmissionsService submissionsService;

    @State({"provider POSTS submission with success",
            "provider POSTS submission with success"})
    public void toPostSubmissionCaseDetailsWithSuccess() throws IOException, JSONException {
        when(submissionsService.submit(anyString(), any(ProbateCaseDetails.class)))
                .thenReturn(getProbateCaseDetails("intestacyGrantOfRepresentation_full.json"));
    }

    @State({"provider POSTS submission with validation errors",
            "provider POSTS submission with validation errors"})
    public void toPostSubmissionCaseDetailsWithValidationErrors() throws IOException, JSONException {
        when(submissionsService.submit(anyString(), any(ProbateCaseDetails.class)))
                .thenReturn(null);
    }
}