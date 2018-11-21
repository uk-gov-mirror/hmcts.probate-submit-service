package uk.gov.hmcts.probate.services.submit.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CaseType {

    @JsonProperty("GrantOfRepresentation") GRANT_OF_REPRESENTATION("GrantOfRepresentation"),
    @JsonProperty("Caveat") CAVEAT("Caveat");

    @Getter
    private final String name;
}


