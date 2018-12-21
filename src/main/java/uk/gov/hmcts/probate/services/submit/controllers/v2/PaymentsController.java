package uk.gov.hmcts.probate.services.submit.controllers.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.services.submit.services.PaymentsService;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.ProbatePaymentDetails;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@Api(tags = {"PaymentsController"})
@SwaggerDefinition(tags = {@Tag(name = "PaymentsController", description = "Payments API")})
@RestController
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsService paymentsService;

    @ApiOperation(value = "Save case draft to CCD", notes = "Save case draft to CCD")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Draft save to CCD successful"),
            @ApiResponse(code = 500, message = "Draft save to CCD  failed")
    })
    @RequestMapping(path = "/payments/{applicantEmail}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ProbateCaseDetails> addPaymentToCase(@PathVariable("applicantEmail") String applicantEmail,
                                                               @Valid @RequestBody ProbatePaymentDetails probatePaymentDetails) {
        return new ResponseEntity(paymentsService.addPaymentToCase(applicantEmail.toLowerCase(), probatePaymentDetails), OK);
    }
}
