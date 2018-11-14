package uk.gov.hmcts.probate.services.submit.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CollectionMember<T> {
    private final String id;
    private final T value;
}
