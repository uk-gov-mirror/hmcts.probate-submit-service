package uk.gov.hmcts.probate.services.submit.model.v2;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class GrantOfRepresentation {

    private LocalDate applicationSubmittedDate;

    private YesNo deceasedDomicileInEngWales;

    private String ihtFormId;

    private YesNo ihtFormCompletedOnline;

    private YesNo softStop;

    private String applicationType;

    private Integer outsideUKGrantCopies;

    private Address deceasedAddress;

    private YesNo deceasedAnyOtherNames;

    private YesNo willHasCodicils;

    private Declaration declaration;

    private final List<CollectionMember<Payment>> payments;

    private final LegalStatement legalStatement;

    private final String deceasedMarriedAfterWillOrCodicilDate;

    private final List<CollectionMember<AliasName>> deceasedAliasNameList;

    private final String primaryApplicantPhoneNumber;


    private final BigDecimal ihtNetValue;

    private final BigDecimal ihtGrossValue;

    private final Long extraCopiesOfGrant;

    private Address primaryApplicantAddress;


}
