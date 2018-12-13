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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseResponse;
import uk.gov.hmcts.probate.services.submit.services.v2.CasesService;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

@Api(tags = {"CasesController"})
@SwaggerDefinition(tags = {@Tag(name = "CasesController", description = "Cases API")})
@RestController
@RequiredArgsConstructor
public class CasesController {

    private final CasesService casesService;

    @ApiOperation(value = "Get case to CCD", notes = "Get case to CCD")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Case retrieval from CCD successful"),
            @ApiResponse(code = 400, message = "Case retrieval from CCD successful")
    })
    @RequestMapping(path = "/cases/{applicantEmail}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CaseResponse> getCase(@RequestParam("caseType") CaseType caseType,
                                                @PathVariable("applicantEmail") String applicantEmail) {
        return ResponseEntity.ok(casesService.getCase(applicantEmail, caseType));
    }
}
