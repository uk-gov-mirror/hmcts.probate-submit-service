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
    private ObjectMapper mapper;

    private static int registryCounter = 1;

    public synchronized JsonNode nextRegistryDataObject(String sequenceNumber) {
        Map<String, String> registryMapper = new HashMap<>();
        Registry nextRegistry = identifyNextRegistry();
        registryMapper.put("sequenceNumber", sequenceNumber);
        registryMapper.put("registrySequenceNumber", Long.toString(getRegistrySequenceNumber(nextRegistry)));
        registryMapper.put("address", nextRegistry.getAddress());
        return mapper.valueToTree(registryMapper);
    }

    long getRegistrySequenceNumber(Registry registry) {
        return persistenceClient.getNextSequenceNumber(registry.getName());
    }

    Registry identifyNextRegistry() {
        Registry nextRegistry =
                registryMap.get(registryCounter % registryMap.size());
        registryCounter++;
        return nextRegistry;
    }
}
