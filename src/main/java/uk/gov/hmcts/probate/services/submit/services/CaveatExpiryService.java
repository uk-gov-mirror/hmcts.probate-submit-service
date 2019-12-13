package uk.gov.hmcts.probate.services.submit.services;

import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;

import java.util.List;

public interface CaveatExpiryService {

    List<ProbateCaseDetails> expireCaveats(String expiryDate);
}
