package uk.gov.hmcts.probate.services.submit.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistryService {

    private final Map<Integer, Registry> registryMap;

    private int registryCounter = 1;

    public void updateRegistry(CaseData caseData) {
        Registry nextRegistry = getNextRegistry();
        log.info("Updating with registry name: {} id: {}", nextRegistry.getName(), nextRegistry.getId());
        caseData.setRegistryLocation(RegistryLocation.findRegistryLocationByName(nextRegistry.getName()));
        if (CaseType.getCaseType(caseData).equals(CaseType.GRANT_OF_REPRESENTATION)){
            GrantOfRepresentationData grantOfRepresentationData = (GrantOfRepresentationData) caseData;
            grantOfRepresentationData.setRegistryAddress(nextRegistry.getAddress());
            grantOfRepresentationData.setRegistryEmailAddress(nextRegistry.getEmail());
            grantOfRepresentationData.setRegistrySequenceNumber(new Long(registryCounter));
        }
    }

    private synchronized Registry getNextRegistry() {
        Registry nextRegistry = registryMap.get(registryCounter % registryMap.size());
        registryCounter++;
        return nextRegistry;
    }
}
