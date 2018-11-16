package uk.gov.hmcts.probate.services.submit.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class GrantOfRepresentation {

    private LocalDate applicationSubmittedDate;

    private YesNo deceasedDomicileInEngWales;

    private String ihtFormId;

    private YesNo ihtFormCompletedOnline;

    private YesNo softStop;

    private String registryLocation;

    private String applicationType;

    private Integer outsideUKGrantCopies;

    private Long extraCopiesOfGrant;

    private YesNo deceasedAnyOtherNames;

    private Long numberOfExecutors;

    private Address deceasedAddress;

    private YesNo willHasCodicils;

    private Long willNumberOfCodicils;

    private Declaration declaration;

    private List<CollectionMember<Payment>> payments;

    private LegalStatement legalStatement;

    private YesNo deceasedMarriedAfterWillOrCodicilDate;

    private String deceasedForenames;

    private String deceasedSurname;

    private LocalDate deceasedDateOfDeath;

    private LocalDate deceasedDateOfBirth;

    private Long numberOfApplicants;

    private YesNo willAccessOriginal;

    private BigDecimal ihtNetValue;

    private BigDecimal ihtGrossValue;

    private String ihtReferenceNumber;

    private String primaryApplicantEmailAddress;

    private Address primaryApplicantAddress;

    private YesNo primaryApplicantIsApplying;

    private YesNo willExists;

    private String primaryApplicantForenames;

    private String primaryApplicantSurname;

    private YesNo primaryApplicantSameWillName;

    private String primaryApplicantAlias;

    private String primaryApplicantAliasReason;
    
    private String primaryApplicantOtherReason;

    private String primaryApplicantPhoneNumber;

    private YesNo willLatestCodicilHasDate;

    @JsonProperty(value = "executorsApplying")
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplying;

    @JsonProperty(value = "executorsNotApplying")
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying;

    private String totalFee;

    private final List<CollectionMember<AliasName>> deceasedAliasNameList;
}
