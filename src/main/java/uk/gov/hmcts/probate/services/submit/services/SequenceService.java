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
    @Autowired
    private PersistenceClient persistenceClient;

    private static int registryCounter = 1;

    public synchronized JsonNode nextRegistryData(long sequenceNumber) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode registryDataObject = mapper.createObjectNode();
        ObjectNode registryMapper = mapper.createObjectNode();
        Registry nextRegistry = identifyNextRegistry();

        registryDataObject.put("submissionReference", Long.toString(sequenceNumber));
        registryMapper.put("name", nextRegistry.capitalizeRegistryName());
        registryMapper.put("sequenceNumber", Long.toString(getRegistrySequenceNumber(nextRegistry)));
        registryMapper.put("email", nextRegistry.getEmail());
        registryMapper.put("address", nextRegistry.getAddress());
        registryDataObject.set("registry", registryMapper);

        return registryDataObject;
    }

    public JsonNode createRegistryDataObject(long submissionReference, JsonNode formData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode registryData = mapper.createObjectNode();
        registryData.put("submissionReference", Long.toString(submissionReference));
        registryData.set("registry", formData.get("formdata").get("registry"));

        return registryData;
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
