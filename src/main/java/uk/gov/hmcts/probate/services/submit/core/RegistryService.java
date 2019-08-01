package uk.gov.hmcts.probate.services.submit.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RegistryService {

    private final Map<Integer, Registry> registryMap;

    private int registryCounter = 1;

    public void updateRegistry(CaseData caseData) {
        Registry nextRegistry = getNextRegistry();
        caseData.setRegistryLocation(RegistryLocation.findRegistryLocationByName(nextRegistry.getName()));
    }

    private synchronized Registry getNextRegistry() {
        Registry nextRegistry = registryMap.get(registryCounter % registryMap.size());
        registryCounter++;
        return nextRegistry;
    }
}
