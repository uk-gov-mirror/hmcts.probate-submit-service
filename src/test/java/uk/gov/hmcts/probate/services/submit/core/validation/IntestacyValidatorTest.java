package uk.gov.hmcts.probate.services.submit.core.validation;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.probate.model.Relationship;
import uk.gov.hmcts.reform.probate.model.cases.AliasName;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.MaritalStatus;
import uk.gov.hmcts.reform.probate.model.cases.ValidatorResults;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SpouseNotApplyingReason;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IntestacyValidatorTest {

    private static final String APPLICANT_EMAIL = "test@test.com";
    private static final String CASE_ID = "12323213323";
    private static final String STATE = "Draft";
    private List<String> errors = new ArrayList<>();
    private GrantOfRepresentationData grantOfRepresentationData;

    LocalDate afterDate = LocalDate.of(2018, 9, 12);
    LocalDate beforeDate = LocalDate.of(1954, 9, 18);

    @Autowired
    private CaseDataValidatorFactory caseDataValidatorFactory;

    private CaseDataValidator<GrantOfRepresentationData> intestacyValidator;

    @Before
    public void setUpTest() {
        grantOfRepresentationData = GrantOfRepresentationCreator.createIntestacyCase();
        intestacyValidator = caseDataValidatorFactory.getValidator(grantOfRepresentationData);
    }


    @Test
    public void shouldRaiseErrorIfDeceasedHasOtherNamesAndAliasListIsNull() {
        grantOfRepresentationData.setDeceasedAnyOtherNames(Boolean.TRUE);
        grantOfRepresentationData.setDeceasedAliasNameList(Collections.emptyList());
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "DeceasedAliasNameList is empty");
    }


    @Test
    public void shouldNotReturnErrorIfDeceasedDoesNotHaveOtherNamesAndAliasListNotPopulated() {
        grantOfRepresentationData.setDeceasedAnyOtherNames(Boolean.FALSE);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }


    @Test
    public void shouldNotRaiseErrorIfDeceasedHasOtherNamesAndAliasListIsPopulated() {
        CollectionMember<AliasName> alias = new CollectionMember<AliasName>();
        grantOfRepresentationData.setDeceasedAnyOtherNames(Boolean.TRUE);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }

    @Test
    public void shouldFailValidationWhenDodIsBeforeDob() {
        grantOfRepresentationData.setDeceasedDateOfBirth(afterDate);
        grantOfRepresentationData.setDeceasedDateOfDeath(beforeDate);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "DeceasedDateOfDeath before DeceasedDateOfBirth");
    }

    @Test
    public void shouldNotRaiseErrorWhenDodIsAfterDob() {

        grantOfRepresentationData.setDeceasedDateOfBirth(beforeDate);
        grantOfRepresentationData.setDeceasedDateOfDeath(afterDate);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }

    @Test
    public void shouldFailValidationWhenRelationshipToDeceasedIsAdoptedChildAndDeceasedOtherChildrenIsNull() {
        grantOfRepresentationData.setPrimaryApplicantRelationshipToDeceased(Relationship.ADOPTED_CHILD);
        grantOfRepresentationData.setDeceasedSpouseNotApplyingReason(SpouseNotApplyingReason.MENTALLY_INCAPABLE);
        grantOfRepresentationData.setPrimaryApplicantAdoptionInEnglandOrWales(Boolean.FALSE);
        grantOfRepresentationData.setDeceasedOtherChildren(null);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "RelationshipToDeceasedIsAdoptedChild and DeceasedOtherChildren is Null");
    }

    @Test
    public void shouldFailValidationWhenRelationshipToDeceasedIsChildAndDeceasedOtherChildrenIsNull() {
        grantOfRepresentationData.setPrimaryApplicantRelationshipToDeceased(Relationship.CHILD);
        grantOfRepresentationData.setDeceasedOtherChildren(null);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "RelationshipToDeceasedIsChild and DeceasedOtherChildren is Null");
    }

    @Test
    public void shouldNotFailValidationWhenRelationshipToDeceasedIsChildAndDeceasedOtherChildrenIsPopulated() {
        grantOfRepresentationData.setPrimaryApplicantRelationshipToDeceased(Relationship.CHILD);
        grantOfRepresentationData.setDeceasedOtherChildren(Boolean.TRUE);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }

    @Test
    public void shouldFailValidationWhenDeceasedMaritalStatusIsDivorcedAndDivorcedInEnglandOrWalesIsNull() {
        grantOfRepresentationData.setDeceasedMartialStatus(MaritalStatus.DIVORCED);
        grantOfRepresentationData.setDeceasedDivorcedInEnglandOrWales(null);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "DeceasedMaritalStatusIsDivorced and DivorcedInEnglandOrWales is Null");
    }

    @Test
    public void shouldFailValidationDeceasedMaritalStatusIsSeparatedAndDivorcedInEnglandOrWalesIsNull() {
        grantOfRepresentationData.setDeceasedMartialStatus(MaritalStatus.JUDICIALLY_SEPARATED);
        grantOfRepresentationData.setDeceasedDivorcedInEnglandOrWales(null);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "DeceasedMaritalStatusIsSeparated and DivorcedInEnglandOrWales is Null");

    }

    @Test
    public void shouldFailValidationWhenDeceasedHasOtherChildrenAndAllDeceasedChildrenOverEighteenIsNull() {
        grantOfRepresentationData.setDeceasedOtherChildren(Boolean.TRUE);
        grantOfRepresentationData.setChildrenOverEighteenSurvived(null);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "DeceasedHasOtherChildren and " +
                "AllDeceasedChildrenOverEighteen is Null");
    }

    @Test
    public void shouldNotFailValidationWhenDeceasedHasOtherChildrenAllDeceasedChildrenOverEighteenIsPopulated() {
        grantOfRepresentationData.setDeceasedOtherChildren(Boolean.TRUE);
        grantOfRepresentationData.setChildrenOverEighteenSurvived(Boolean.FALSE);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }

    @Test
    public void shouldFailWhenDeceasedHasOtherChildrenAndDeceasedHasChildrenOverEighteenAndChildrenDiedIsNull() {
        grantOfRepresentationData.setDeceasedOtherChildren(Boolean.TRUE);
        grantOfRepresentationData.setChildrenOverEighteenSurvived(Boolean.TRUE);
        grantOfRepresentationData.setChildrenDied(null);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "ChildrenDied is Null");
    }

    @Test
    public void shouldFailWhenDeceasedHasOtherChildrenAndDeceasedGrandchildrenUnderEighteenIsNull() {
        grantOfRepresentationData.setDeceasedOtherChildren(Boolean.TRUE);
        grantOfRepresentationData.setChildrenOverEighteenSurvived(Boolean.TRUE);
        grantOfRepresentationData.setChildrenDied(Boolean.TRUE);
        grantOfRepresentationData.setGrandChildrenSurvivedUnderEighteen(null);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "GrandChildrenSurvivedUnderEighteen is Null");
    }

    @Test
    public void shouldFailValidationWhenAssetsOverseasNotPopulatedAndIhtNetValueLessThanOrEqualTo250000() {
        grantOfRepresentationData.setIhtNetValue(250000L);
        grantOfRepresentationData.setDeceasedHasAssetsOutsideUK(null);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "DeceasedHasAssetsOutsideUK is Null");
    }

    @Test
    public void shouldPassValidationWhenAssetsOverseasNotPopulatedAndIhtNetValueMoreThan250000() {
        grantOfRepresentationData.setIhtNetValue(250001L);
        grantOfRepresentationData.setIhtGrossValue(250002L);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        Assertions.assertThat(validateResults.getValidationMessages()).isEmpty();
    }

    @Test
    public void shouldRaiseMultipleConstraintViolations() {
        grantOfRepresentationData.setDeceasedMartialStatus(MaritalStatus.JUDICIALLY_SEPARATED);
        grantOfRepresentationData.setPrimaryApplicantRelationshipToDeceased(Relationship.CHILD);
        grantOfRepresentationData.setDeceasedOtherChildren(Boolean.TRUE);
        grantOfRepresentationData.setChildrenOverEighteenSurvived(null);
        grantOfRepresentationData.setDeceasedDateOfBirth(afterDate);
        grantOfRepresentationData.setDeceasedDateOfDeath(beforeDate);
        ValidatorResults validateResults = intestacyValidator.validate(grantOfRepresentationData);
        assertValidationErrorMessage(validateResults, "DeceasedDateOfDeath before DeceasedDateOfBirth");
        assertValidationErrorMessage(validateResults, "DeceasedHasOtherChildren and AllDeceasedChildrenOverEighteen is Null");
    }


    private void assertValidationErrorMessage(ValidatorResults validateResults, String validationMessage) {
        Assertions.assertThat(validateResults.getValidationMessages()).isNotEmpty();
        Assertions.assertThat(validateResults.getValidationMessages()).contains(validationMessage);
    }
}
