package uk.gov.hmcts.probate.services.submit.controllers.v2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.probate.services.submit.services.DraftService;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {DraftsController.class}, secure = false)
public class DraftsControllerTest {

    private static final String DRAFTS_URL = "/case-type/GrantOfRepresentation/drafts";

    private static final String EMAIL_ADDRESS = "test@test.com";

    @MockBean
    private DraftService draftService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldSaveDraft() throws Exception {
        String json = TestUtils.getJSONFromFile("success.pa.ccd.json");

        mockMvc.perform(post(DRAFTS_URL + "/" + EMAIL_ADDRESS)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
