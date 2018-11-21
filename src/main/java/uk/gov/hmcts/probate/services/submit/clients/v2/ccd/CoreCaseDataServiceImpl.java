package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseData;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseType;
import uk.gov.hmcts.probate.services.submit.model.v2.CcdInfo;
import uk.gov.hmcts.probate.services.submit.model.v2.DraftResponse;
import uk.gov.hmcts.probate.services.submit.model.v2.JurisdictionId;
import uk.gov.hmcts.probate.services.submit.services.v2.CoreCaseDataService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import java.util.List;
import java.util.Optional;

@Component
public class CoreCaseDataServiceImpl implements CoreCaseDataService {

    private static final String CASE_QUERY_PARAM = "case.primaryApplicantEmailAddress";

    private final SecurityUtils securityUtils;

    private final CoreCaseDataApi coreCaseDataApi;

    @Autowired
    public CoreCaseDataServiceImpl(CoreCaseDataApi coreCaseDataApi, SecurityUtils securityUtils) {
        this.coreCaseDataApi = coreCaseDataApi;
        this.securityUtils = securityUtils;
    }

    @Override
    public DraftResponse saveDraft(String applicantEmail, CaseData caseData) {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();

        Optional<CaseDetails> caseDetailsOptional = getCase(applicantEmail, caseData.getType(), securityDTO);

        if (caseDetailsOptional.isPresent()) {
            CaseDetails caseDetails = caseDetailsOptional.get();
            updateCase(caseDetails.getId().toString(), caseData, EventId.UPDATE_DRAFT, securityDTO);
        }
        createCase(caseData, EventId.CREATE_DRAFT, securityDTO);
        return null;
    }

    private DraftResponse updateCase(String caseId, CaseData caseData, EventId eventId, SecurityDTO securityDTO) {
        CaseType caseType = caseData.getType();
        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCitizen(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                JurisdictionId.PROBATE.name(),
                caseType.getName(),
                caseId,
                eventId.getName()
        );
        CaseDataContent caseDataContent = createCaseDataContent(caseData, eventId, startEventResponse);
        CaseDetails caseDetails = coreCaseDataApi.submitEventForCitizen(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                JurisdictionId.PROBATE.name(),
                caseType.getName(),
                caseId,
                false,
                caseDataContent
        );
        return createDraftResponse(caseData, caseDetails);
    }

    private DraftResponse createCase(CaseData caseData, EventId eventId, SecurityDTO securityDTO) {
        CaseType caseType = caseData.getType();
        StartEventResponse startEventResponse = coreCaseDataApi.startForCitizen(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                JurisdictionId.PROBATE.name(),
                caseType.getName(),
                eventId.getName()
        );
        CaseDataContent caseDataContent = createCaseDataContent(caseData, eventId, startEventResponse);
        CaseDetails caseDetails = coreCaseDataApi.submitForCitizen(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                JurisdictionId.PROBATE.name(),
                caseType.getName(),
                false,
                caseDataContent
        );
        return createDraftResponse(caseData, caseDetails);
    }

    private DraftResponse createDraftResponse(CaseData caseData, CaseDetails caseDetails) {
        return DraftResponse.builder()
                .caseData(caseData)
                .ccdInfo(CcdInfo.builder()
                        .caseId(caseDetails.getId().toString())
                        .state(caseDetails.getState())
                        .build()
                ).build();
    }

    private CaseDataContent createCaseDataContent(CaseData caseData, EventId eventId, StartEventResponse startEventResponse) {
        return CaseDataContent.builder()
                .event(createEvent(eventId))
                .eventToken(startEventResponse.getToken())
                .data(caseData)
                .build();
    }

    private Event createEvent(EventId eventId) {
        return Event.builder()
                .id(eventId.getName())
                .description("Probate application")
                .summary("Probate application")
                .build();
    }

    private Optional<CaseDetails> getCase(String applicantEmail, CaseType caseType, SecurityDTO securityDTO) {
        List<CaseDetails> caseDetails = coreCaseDataApi.searchForCitizen(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                JurisdictionId.PROBATE.name(),
                caseType.getName(),
                ImmutableMap.of(CASE_QUERY_PARAM, applicantEmail));
        if (caseDetails == null) {
            return Optional.empty();
        }
        if (caseDetails.size() > 1) {
            throw new IllegalStateException("Multiple cases exist with applicant email provided!");
        }
        return caseDetails.stream().findFirst();
    }
}
