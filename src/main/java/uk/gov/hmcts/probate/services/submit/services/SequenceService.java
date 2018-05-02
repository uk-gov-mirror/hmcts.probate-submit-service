package uk.gov.hmcts.probate.services.submit.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private ObjectMapper mapper = new ObjectMapper();

    private static int registryCounter = 1;

    public synchronized JsonNode nextRegistryDataObject(String sequenceNumber) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode registryMapper = mapper.createObjectNode();
        Registry nextRegistry = identifyNextRegistry();
        registryMapper.put("submissionReference", sequenceNumber);
        registryMapper.put("registryName", nextRegistry.getName());
        registryMapper.put("registrySequenceNumber", Long.toString(getRegistrySequenceNumber(nextRegistry)));
        registryMapper.put("address", nextRegistry.getAddress());
        return registryMapper;
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
