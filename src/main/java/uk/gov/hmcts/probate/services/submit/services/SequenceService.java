package uk.gov.hmcts.probate.services.submit.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.probate.services.submit.clients.PersistenceClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class SequenceService {
    @Autowired
    Map<Integer, Registry> registryMap;
    private PersistenceClient persistenceClient;
    private static int registryCounter = 1;
    private ObjectMapper mapper;

    public long getRegistrySequenceNumber() {
        String nextRegistryName = identifyNextRegistry();
        return persistenceClient.getNextSequenceNumber(nextRegistryName);
    }

    public String identifyNextRegistry() {
        Registry nextRegistry =
                registryMap.get(registryCounter % registryMap.size());
        registryCounter++;
        return nextRegistry.getName();
    }

    public JsonNode getRegistryDataObject(Registry registry) {
        Map<String, String> registryMapper = new HashMap<>();
        registryMapper.put("name", registry.getName());
        registryMapper.put("email", registry.getEmail());
        registryMapper.put("address", registry.getAddress());
        return mapper.valueToTree(registryMapper);
    }
}
