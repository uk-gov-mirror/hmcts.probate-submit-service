package uk.gov.hmcts.probate.services.caseaccess;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.FindUserCaseRolesRequest;
import uk.gov.hmcts.probate.model.FindUserCaseRolesResponse;
import uk.gov.hmcts.probate.security.SecurityUtils;

import java.util.List;

@Component("ccdDataStoreService")
@Slf4j
@RequiredArgsConstructor
public class CcdDataStoreService {

    private final SecurityUtils securityUtils;
    private final CaseRoleClient caseRoleClient;

    public boolean isCaseOwner(String caseId) {
        log.info("isCaseOwner caseId:: {}", caseId.toLowerCase());
        FindUserCaseRolesResponse response = findUserCaseRoles(caseId.toLowerCase());
        return response.getCaseUsers().stream()
                .anyMatch(caseUser -> "[CREATOR]".equals(caseUser.getCaseRole()));
    }

    public FindUserCaseRolesResponse findUserCaseRoles(String caseId) {

        return caseRoleClient.findUserCaseRoles(
                securityUtils.getUserToken(),
                securityUtils.generateServiceToken(),
            buildFindUserCaseRolesRequest(caseId,securityUtils.getUserId())
        );
    }

    private FindUserCaseRolesRequest buildFindUserCaseRolesRequest(String caseId, String userId) {
        return FindUserCaseRolesRequest
            .builder()
            .caseIds(List.of(caseId))
            .userIds(List.of(userId))
            .build();
    }
}
