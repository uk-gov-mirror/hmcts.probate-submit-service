package uk.gov.hmcts.probate.services.submit.core;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseValidationException;
import uk.gov.hmcts.reform.probate.model.cases.CaseInfo;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
import uk.gov.hmcts.reform.probate.model.validation.groups.crossfieldcheck.IntestacyCrossFieldCheck;
import uk.gov.hmcts.reform.probate.model.validation.groups.crossfieldcheck.PaCrossFieldCheck;
import uk.gov.hmcts.reform.probate.model.validation.groups.fieldcheck.IntestacyFieldCheck;
import uk.gov.hmcts.reform.probate.model.validation.groups.fieldcheck.PaFieldCheck;
import uk.gov.hmcts.reform.probate.model.validation.groups.nullcheck.IntestacyNullCheck;
import uk.gov.hmcts.reform.probate.model.validation.groups.nullcheck.PaNullCheck;
import uk.gov.hmcts.reform.probate.model.validation.groups.submission.IntestacySubmission;
import uk.gov.hmcts.reform.probate.model.validation.groups.submission.PaSubmission;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ValidationServiceImplTest {

    private Class[] PA_VALIDATION_GROUPS = {PaNullCheck.class, PaFieldCheck.class, PaCrossFieldCheck.class};

    private Class[] PA_SUBMISSION_GROUPS = {PaNullCheck.class, PaFieldCheck.class, PaCrossFieldCheck.class, PaSubmission.class};

    private Class[] INTESTACY_VALIDATION_GROUPS = {IntestacyNullCheck.class, IntestacyFieldCheck.class, IntestacyCrossFieldCheck.class};

    private Class[] INTESTACY_SUBMISSION_GROUPS = {IntestacyNullCheck.class, IntestacyFieldCheck.class, IntestacyCrossFieldCheck.class, IntestacySubmission.class};

    private Class[] CAVEAT_VALIDATION_GROUPS = {Default.class};

    private Class[] CAVEAT_SUBMISSION_GROUPS = {Default.class};


    private Validator validator;

    private ValidationServiceImpl validationService;

    @Before
    public void setUp() {
        validator = Mockito.mock(Validator.class);
        validationService = new ValidationServiceImpl(validator);
    }

    @Test
    public void shouldValidateForGrantOfRepresentationGrantOfProbate() {
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .grantType(GrantType.GRANT_OF_PROBATE)
            .build();
        CaseInfo caseInfo = CaseInfo.builder().build();

        ProbateCaseDetails probateCaseDetails = ProbateCaseDetails.builder()
            .caseData(grantOfRepresentationData)
            .caseInfo(caseInfo)
            .build();

        when(validator.validate(grantOfRepresentationData, PA_VALIDATION_GROUPS)).thenReturn(Sets.newHashSet());

        validationService.validate(probateCaseDetails);

        verify(validator, times(1)).validate(grantOfRepresentationData, PA_VALIDATION_GROUPS);
    }

    @Test
    public void shouldValidateForSubmissionGrantOfRepresentationGrantOfProbate() {
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .grantType(GrantType.GRANT_OF_PROBATE)
            .build();
        CaseInfo caseInfo = CaseInfo.builder().build();

        ProbateCaseDetails probateCaseDetails = ProbateCaseDetails.builder()
            .caseData(grantOfRepresentationData)
            .caseInfo(caseInfo)
            .build();

        when(validator.validate(grantOfRepresentationData, PA_SUBMISSION_GROUPS)).thenReturn(Sets.newHashSet());

        validationService.validateForSubmission(probateCaseDetails);

        verify(validator, times(1)).validate(grantOfRepresentationData, PA_SUBMISSION_GROUPS);
    }

    @Test(expected = CaseValidationException.class)
    public void shouldThrowCaseValidationExceptionOnValidateWhenConstraintViolationsExist() {
        ConstraintViolation<GrantOfRepresentationData> constraintViolation = Mockito.mock(ConstraintViolation.class);
        Set<ConstraintViolation<GrantOfRepresentationData>> constraintViolations = new HashSet<>();
        constraintViolations.add(constraintViolation);

        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .grantType(GrantType.GRANT_OF_PROBATE)
            .build();
        CaseInfo caseInfo = CaseInfo.builder().build();

        ProbateCaseDetails probateCaseDetails = ProbateCaseDetails.builder()
            .caseData(grantOfRepresentationData)
            .caseInfo(caseInfo)
            .build();

        when(validator.validate(grantOfRepresentationData, PA_VALIDATION_GROUPS)).thenReturn(constraintViolations);

        validationService.validate(probateCaseDetails);

        verify(validator, times(1)).validate(grantOfRepresentationData, PA_VALIDATION_GROUPS);
    }

    @Test
    public void shouldValidateForGrantOfRepresentationGrantOfIntestacy() {
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .grantType(GrantType.INTESTACY)
            .build();
        CaseInfo caseInfo = CaseInfo.builder().build();

        ProbateCaseDetails probateCaseDetails = ProbateCaseDetails.builder()
            .caseData(grantOfRepresentationData)
            .caseInfo(caseInfo)
            .build();

        when(validator.validate(grantOfRepresentationData, INTESTACY_VALIDATION_GROUPS)).thenReturn(Sets.newHashSet());

        validationService.validate(probateCaseDetails);

        verify(validator, times(1)).validate(grantOfRepresentationData, INTESTACY_VALIDATION_GROUPS);
    }

    @Test
    public void shouldValidateForSubmissionGrantOfRepresentationIntestacy() {
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .grantType(GrantType.INTESTACY)
            .build();
        CaseInfo caseInfo = CaseInfo.builder().build();

        ProbateCaseDetails probateCaseDetails = ProbateCaseDetails.builder()
            .caseData(grantOfRepresentationData)
            .caseInfo(caseInfo)
            .build();

        when(validator.validate(grantOfRepresentationData, INTESTACY_SUBMISSION_GROUPS)).thenReturn(Sets.newHashSet());

        validationService.validateForSubmission(probateCaseDetails);

        verify(validator, times(1)).validate(grantOfRepresentationData, INTESTACY_SUBMISSION_GROUPS);
    }


    @Test
    public void shouldValidateForCaveat() {
        CaveatData caveatData = CaveatData.builder().build();
        CaseInfo caseInfo = CaseInfo.builder().build();

        ProbateCaseDetails probateCaseDetails = ProbateCaseDetails.builder()
            .caseData(caveatData)
            .caseInfo(caseInfo)
            .build();

        when(validator.validate(caveatData, CAVEAT_VALIDATION_GROUPS)).thenReturn(Sets.newHashSet());

        validationService.validate(probateCaseDetails);

        verify(validator, times(1)).validate(caveatData, CAVEAT_VALIDATION_GROUPS);
    }

    @Test
    public void shouldValidateForSubmissionCaveat() {
        CaveatData caveatData = CaveatData.builder().build();
        CaseInfo caseInfo = CaseInfo.builder().build();

        ProbateCaseDetails probateCaseDetails = ProbateCaseDetails.builder()
            .caseData(caveatData)
            .caseInfo(caseInfo)
            .build();

        when(validator.validate(caveatData, CAVEAT_SUBMISSION_GROUPS)).thenReturn(Sets.newHashSet());

        validationService.validateForSubmission(probateCaseDetails);

        verify(validator, times(1)).validate(caveatData, CAVEAT_SUBMISSION_GROUPS);
    }
}
