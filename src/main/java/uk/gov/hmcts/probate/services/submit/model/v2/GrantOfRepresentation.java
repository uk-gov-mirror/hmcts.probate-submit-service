package uk.gov.hmcts.probate.services.submit.model.v2;

import lombok.Data;

import java.util.List;

@Data
public class GrantOfRepresentation {

    private String applicationType;

    private Integer outsideUKGrantCopies;

    private Address deceasedAddress;

    private YesNo deceasedAnyOtherNames;

    private YesNo willHasCodicils;

    private Declaration declaration;

    private final List<CollectionMember<Payment>> payments;

}
