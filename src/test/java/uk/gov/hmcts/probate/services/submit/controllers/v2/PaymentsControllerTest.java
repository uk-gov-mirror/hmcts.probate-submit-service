package uk.gov.hmcts.probate.services.submit.controllers.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;
import uk.gov.hmcts.probate.services.submit.model.v2.PaymentUpdateRequest;
import uk.gov.hmcts.probate.services.submit.services.v2.PaymentsService;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseInfo;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {PaymentsController.class}, secure = false)
public class PaymentsControllerTest {

    private static final String PAYMENTS_URL = "/v2/payments";
    private static final String EMAIL_ADDRESS = "test@test.com";
    private static final String CASE_ID = "1343242352";
    private static final String APPLICATION_CREATED = "PAAppCreated";

    @MockBean
    private PaymentsService paymentsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldAddPaymentToCase() throws Exception {
        String json = TestUtils.getJSONFromFile("files/v2/intestacyGrantOfRepresentation.json");
        CaseData grantOfRepresentation = objectMapper.readValue(json, CaseData.class);
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setCaseId(CASE_ID);
        caseInfo.setState(APPLICATION_CREATED);
        CasePayment payment = grantOfRepresentation.getPayments().get(0).getValue();
        CaseResponse caseResponse = CaseResponse.builder().caseInfo(caseInfo).caseData(grantOfRepresentation).build();
        PaymentUpdateRequest paymentUpdateRequest = PaymentUpdateRequest.builder()
                .payment(payment)
                .type(CaseType.GRANT_OF_REPRESENTATION)
                .build();
        when(paymentsService.addPaymentToCase(eq(EMAIL_ADDRESS), eq(paymentUpdateRequest))).thenReturn(caseResponse);

        mockMvc.perform(post(PAYMENTS_URL + "/" + EMAIL_ADDRESS)
                .content(objectMapper.writeValueAsString(paymentUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(paymentsService).addPaymentToCase(eq(EMAIL_ADDRESS), eq(paymentUpdateRequest));
    }
}