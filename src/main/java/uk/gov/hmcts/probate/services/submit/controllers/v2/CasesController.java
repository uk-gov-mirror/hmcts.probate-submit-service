package uk.gov.hmcts.probate.services.submit.controllers.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.services.submit.services.CasesService;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
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
    @GetMapping(path = "/cases/{applicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ProbateCaseDetails> getCase(@RequestParam("caseType") CaseType caseType,
                                                      @PathVariable("applicationId") String applicationId) {
        log.info("Retrieving case of caseType: {}", caseType.getName());
        return ResponseEntity.ok(casesService.getCase(applicationId.toLowerCase(), caseType));
    }

    @PostMapping(path = "/cases/{applicationId}", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ProbateCaseDetails> saveCase(@PathVariable("applicationId") String applicationId,
                                                        @RequestBody ProbateCaseDetails caseRequest) {
        log.info("Saving draft for case type: {}", caseRequest.getCaseData().getClass().getSimpleName());
        return new ResponseEntity(casesService.saveCase(applicationId.toLowerCase(), caseRequest), OK);
    }
}
