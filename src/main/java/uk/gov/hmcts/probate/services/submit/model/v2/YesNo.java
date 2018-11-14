package uk.gov.hmcts.probate.services.submit.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum YesNo {

    @JsonProperty("Yes") YES,
    @JsonProperty("No") NO
}
