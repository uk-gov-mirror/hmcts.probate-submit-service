package uk.gov.hmcts.probate.services.submit.controllers.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseValidationException;
import uk.gov.hmcts.probate.services.submit.services.CasesService;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseInfo;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {CasesController.class}, secure = false)
public class CasesControllerTest {

    private static final String CASES_URL = "/cases";
    private static final String CASES_INVITATION_URL = "/cases/invitation";
    private static final String EMAIL_ADDRESS = "test@test.com";
    private static final String INVITATION_ID = "invitationId";
    private static final String CASE_ID = "1343242352";
    private static final String DRAFT = "Draft";
    private static final String VALIDATE_ENDPOINT = "/validations";

    @MockBean
    private CasesService casesService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldGetCaseForIntestacyGrantOfRepresentation() throws Exception {
        String json = TestUtils.getJSONFromFile("files/v2/intestacyGrantOfRepresentation.json");
        CaseData grantOfRepresentation = objectMapper.readValue(json, CaseData.class);
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setCaseId(CASE_ID);
        caseInfo.setState(CaseState.DRAFT);
        ProbateCaseDetails caseResponse = ProbateCaseDetails.builder().caseInfo(caseInfo).caseData(grantOfRepresentation).build();
        when(casesService.getCase(EMAIL_ADDRESS, CaseType.GRANT_OF_REPRESENTATION)).thenReturn(caseResponse);

        mockMvc.perform(get(CASES_URL + "/" + EMAIL_ADDRESS)
                .param("caseType", CaseType.GRANT_OF_REPRESENTATION.name())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(casesService, times(1)).getCase(EMAIL_ADDRESS, CaseType.GRANT_OF_REPRESENTATION);
    }

    @Test
    public void shouldGetCaseByIdForIntestacyGrantOfRepresentation() throws Exception {
        String json = TestUtils.getJSONFromFile("files/v2/intestacyGrantOfRepresentation.json");
        CaseData grantOfRepresentation = objectMapper.readValue(json, CaseData.class);
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setCaseId(CASE_ID);
        caseInfo.setState(CaseState.DRAFT);
        ProbateCaseDetails caseResponse = ProbateCaseDetails.builder().caseInfo(caseInfo).caseData(grantOfRepresentation).build();
        when(casesService.getCaseById(CASE_ID)).thenReturn(caseResponse);

        mockMvc.perform(get(CASES_URL)
                .param("caseId", CASE_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(casesService, times(1)).getCaseById(CASE_ID);
    }

    @Test
    public void shouldGetCaseForIntestacyGrantOfRepresentationByInvitationId() throws Exception {

        ProbateCaseDetails probateCaseDetails = ProbateCaseDetails.builder().build();
        when(casesService.getCaseByInvitationId(INVITATION_ID, CaseType.GRANT_OF_REPRESENTATION)).thenReturn(probateCaseDetails);

        mockMvc.perform(get(CASES_INVITATION_URL + "/" + INVITATION_ID)
                .param("caseType", CaseType.GRANT_OF_REPRESENTATION.name())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(casesService, times(1)).getCaseByInvitationId(INVITATION_ID, CaseType.GRANT_OF_REPRESENTATION);
    }


    @Test
    public void shouldValidate() throws Exception {
        CaseData caseData = GrantOfRepresentationData.builder().grantType(GrantType.GRANT_OF_PROBATE).build();

        mockMvc.perform(put(CASES_URL + "/" + EMAIL_ADDRESS + VALIDATE_ENDPOINT)
            .param("caseType", CaseType.GRANT_OF_REPRESENTATION.name())
            .content(objectMapper.writeValueAsString(caseData))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(casesService, times(1)).validate(EMAIL_ADDRESS, CaseType.GRANT_OF_REPRESENTATION);
    }

    @Test
    public void shouldReturnStatusOf400WhenCaseValidationException() throws Exception {
        ConstraintViolation<CaseData> constraintViolation = Mockito.mock(ConstraintViolation.class);
        when(constraintViolation.getMessage()).thenReturn("must not be null");
        Path path = Mockito.mock(Path.class);
        when(path.toString()).thenReturn("fieldName");
        when(constraintViolation.getPropertyPath()).thenReturn(path);

        Set<ConstraintViolation<CaseData>> constraintViolations = new HashSet<>();
        constraintViolations.add(constraintViolation);
        CaseValidationException caseValidationException = new CaseValidationException(constraintViolations);

        when(casesService.validate(EMAIL_ADDRESS, CaseType.GRANT_OF_REPRESENTATION)).thenThrow(caseValidationException);

        mockMvc.perform(put(CASES_URL + "/" + EMAIL_ADDRESS + VALIDATE_ENDPOINT)
            .param("caseType", CaseType.GRANT_OF_REPRESENTATION.name())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].field", is("fieldName")))
            .andExpect(jsonPath("$.errors[0].message", is("must not be null")))
            .andExpect(status().isBadRequest());

        verify(casesService, times(1)).validate(EMAIL_ADDRESS, CaseType.GRANT_OF_REPRESENTATION);
    }
}
