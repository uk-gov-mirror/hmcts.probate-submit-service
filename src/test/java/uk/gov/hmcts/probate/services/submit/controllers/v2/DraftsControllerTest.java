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
import uk.gov.hmcts.probate.services.submit.model.v2.CaseRequest;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;
import uk.gov.hmcts.probate.services.submit.services.v2.DraftService;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseInfo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {DraftsController.class}, secure = false)
public class DraftsControllerTest {

    private static final String DRAFTS_URL = "/drafts";

    private static final String EMAIL_ADDRESS = "test@test.com";
    private static final String CASE_ID = "1343242352";
    private static final String DRAFT = "Draft";
    private static final String DECEASED_FORENAMES = "Ned";
    private static final String DECEASED_SURNAME = "Stark";

    @MockBean
    private DraftService draftService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldSaveDraft() throws Exception {
        String json = TestUtils.getJSONFromFile("success.pa.ccd.json");
        CaseData grantOfRepresentation = objectMapper.readValue(json, CaseData.class);
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setCaseId(CASE_ID);
        caseInfo.setState(DRAFT);
        CaseResponse caseResponse = CaseResponse.builder().caseInfo(caseInfo).caseData(grantOfRepresentation).build();
        when(draftService.saveDraft(eq(EMAIL_ADDRESS), any(CaseRequest.class))).thenReturn(caseResponse);

        CaseRequest caseRequest = CaseRequest.builder().caseData(grantOfRepresentation).build();
        mockMvc.perform(post(DRAFTS_URL + "/" + EMAIL_ADDRESS)
                .content(objectMapper.writeValueAsString(caseRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
