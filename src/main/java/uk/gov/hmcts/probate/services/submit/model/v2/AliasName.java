package uk.gov.hmcts.probate.services.submit.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AliasName {

    @JsonProperty(value = "Forenames")
    private final String forenames;

    @JsonProperty(value = "LastName")
    private final String lastName;

    @JsonProperty(value = "AppearOnGrant")
    private final String appearOnGrant;

}
