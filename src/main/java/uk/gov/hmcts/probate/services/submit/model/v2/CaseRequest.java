package uk.gov.hmcts.probate.services.submit.model.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;

import javax.validation.Valid;

@Data
@Builder
@ApiModel(value = "CaseRequest", description = "Case request which contains specific case data to save on CCD")
public class CaseRequest {

    @ApiModelProperty(value = "Case data to send to CCD, which will be of a specific type eg. GrantOfRepresentation")
    @Valid
    private CaseData caseData;
}
