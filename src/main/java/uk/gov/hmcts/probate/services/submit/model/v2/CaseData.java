package uk.gov.hmcts.probate.services.submit.model.v2;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import uk.gov.hmcts.probate.services.submit.model.v2.grantofrepresentation.GrantOfRepresentation;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = GrantOfRepresentation.class, name = "GrantOfRepresentation")})
@Data
public abstract class CaseData {
}
