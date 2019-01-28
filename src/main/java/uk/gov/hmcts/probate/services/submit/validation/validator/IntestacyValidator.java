package uk.gov.hmcts.probate.services.submit.validation.validator;

import uk.gov.hmcts.probate.services.submit.validation.ValidationRule;
import uk.gov.hmcts.reform.probate.model.Relationship;
import uk.gov.hmcts.reform.probate.model.cases.MaritalStatus;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.Arrays;
import java.util.List;

public class IntestacyValidator extends CaseDataValidator<GrantOfRepresentationData> {

    private static ValidationRule<GrantOfRepresentationData> isAliasNameListPopulated() {
        return ValidationRule.from(gop ->
                (!gop.getDeceasedAnyOtherNames() || (gop.getDeceasedAnyOtherNames()
                        && gop.getDeceasedAliasNameList() != null
                        && !gop.getDeceasedAliasNameList().isEmpty())), "DeceasedAliasNameList is empty");
    }

    private static ValidationRule<GrantOfRepresentationData> isDeceasedAssetsOutsideUKPopulated() {
        return ValidationRule.from(gop ->
                        (gop.getIhtNetValue() <= 2500000 && gop.getDeceasedHasAssetsOutsideUK() != null)
                , "DeceasedHasAssetsOutsideUK is Null");
    }

    private static ValidationRule<GrantOfRepresentationData> isDeceasedDateOfDeathAfterDateOfBirth() {
        return ValidationRule.from(gop ->
                        gop.getDeceasedDateOfDeath().isAfter(gop.getDeceasedDateOfBirth())
                , "DeceasedDateOfDeath before DeceasedDateOfBirth");
    }

    private static ValidationRule<GrantOfRepresentationData>
    isDeceasedOtherChildPopulatedWhenRelationshipToDeceasedIsAdoptedChild() {
        return ValidationRule.from(gop ->
                        !gop.getPrimaryApplicantRelationshipToDeceased().equals(Relationship.ADOPTED_CHILD)
                                || (gop.getPrimaryApplicantRelationshipToDeceased().equals(Relationship.ADOPTED_CHILD)
                                && !gop.getPrimaryApplicantAdoptionInEnglandOrWales()
                                && gop.getDeceasedOtherChildren() != null)
                , "RelationshipToDeceasedIsAdoptedChild and DeceasedOtherChildren is Null");
    }

    private static ValidationRule<GrantOfRepresentationData>
    isDeceasedOtherChildPopulatedWhenRelationshipToDeceasedIsChild() {
        return ValidationRule.from(gop ->
                        !gop.getPrimaryApplicantRelationshipToDeceased().equals(Relationship.CHILD)
                                || (gop.getPrimaryApplicantRelationshipToDeceased().equals(Relationship.CHILD)
                                && gop.getDeceasedOtherChildren() != null)
                , "RelationshipToDeceasedIsChild and DeceasedOtherChildren is Null");
    }

    private static ValidationRule<GrantOfRepresentationData>
    isDivorcedInEnglandOrWalesPopulatedWhenDeceasedDivorced() {
        return ValidationRule.from(gop ->
                        !gop.getDeceasedMartialStatus().equals(MaritalStatus.DIVORCED)
                                || (gop.getDeceasedMartialStatus().equals(MaritalStatus.DIVORCED)
                                && gop.getDeceasedDivorcedInEnglandOrWales() != null)
                , "DeceasedMaritalStatusIsDivorced and DivorcedInEnglandOrWales is Null");
    }

    private static ValidationRule<GrantOfRepresentationData>
    isDivorcedInEnglandOrWalesPopulatedWhenDeceasedSeperated() {
        return ValidationRule.from(gop ->
                        !gop.getDeceasedMartialStatus().equals(MaritalStatus.JUDICIALLY_SEPARATED)
                                || (gop.getDeceasedMartialStatus().equals(MaritalStatus.JUDICIALLY_SEPARATED)
                                && gop.getDeceasedDivorcedInEnglandOrWales() != null)
                , "DeceasedMaritalStatusIsSeparated and DivorcedInEnglandOrWales is Null");
    }

    private static ValidationRule<GrantOfRepresentationData>
    isAllDeceasedChildrenOverEighteenPopulatedWhenDeceasedHasOtherChildren() {
        return ValidationRule.from(gop ->
                        isFalseOrNull(gop.getDeceasedOtherChildren()) || (gop.getDeceasedOtherChildren()
                                && gop.getChildrenOverEighteenSurvived() != null)
                , "DeceasedHasOtherChildren and AllDeceasedChildrenOverEighteen is Null");
    }

    private static ValidationRule<GrantOfRepresentationData> isChildrenDiedPopulatedWhenDeceasedHasOtherChildren() {
        return ValidationRule.from(gop ->
                        (isFalseOrNull(gop.getDeceasedOtherChildren()) ||
                                (gop.getDeceasedOtherChildren() && gop.getChildrenDied() != null))
                , "ChildrenDied is Null");
    }

    private static ValidationRule<GrantOfRepresentationData>
    isGrandChildrenSurvivedUnderEighteenPopulatedWhenMandatory() {
        return ValidationRule.from(gop ->
                        (isFalseOrNull(gop.getDeceasedOtherChildren())
                                || isFalseOrNull(gop.getChildrenOverEighteenSurvived())
                                || isFalseOrNull(gop.getChildrenDied()))
                                || (gop.getDeceasedOtherChildren() && gop.getChildrenDied()
                                && gop.getChildrenDied() && gop.getGrandChildrenSurvivedUnderEighteen() != null)
                , "GrandChildrenSurvivedUnderEighteen is Null");
    }

    private static Boolean isFalseOrNull(Boolean value) {
        return value == null || !value.booleanValue();
    }

    @Override
    List<ValidationRule<GrantOfRepresentationData>> getRules() {
        return Arrays.asList(
                IntestacyValidator.isAliasNameListPopulated(),
                IntestacyValidator.isDeceasedAssetsOutsideUKPopulated(),
                IntestacyValidator.isDeceasedDateOfDeathAfterDateOfBirth(),
                IntestacyValidator.isAllDeceasedChildrenOverEighteenPopulatedWhenDeceasedHasOtherChildren(),
                IntestacyValidator.isChildrenDiedPopulatedWhenDeceasedHasOtherChildren(),
                IntestacyValidator.isDeceasedOtherChildPopulatedWhenRelationshipToDeceasedIsAdoptedChild(),
                IntestacyValidator.isDivorcedInEnglandOrWalesPopulatedWhenDeceasedDivorced(),
                IntestacyValidator.isDeceasedOtherChildPopulatedWhenRelationshipToDeceasedIsChild(),
                IntestacyValidator.isDivorcedInEnglandOrWalesPopulatedWhenDeceasedSeperated(),
                IntestacyValidator.isGrandChildrenSurvivedUnderEighteenPopulatedWhenMandatory()
        );
    }
}
