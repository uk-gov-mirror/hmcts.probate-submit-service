package uk.gov.hmcts.probate.services.submit.services;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.probate.services.submit.clients.PersistenceClient;

@Service
public class SequenceService {
    @Autowired
    Map<Integer, Registry> registryMap;
    @Autowired
    private PersistenceClient persistenceClient;
    @Autowired
    private JavaMailSenderImpl mailSender;

    private static int registryCounter = 1;

    public synchronized JsonNode nextRegistry(long submissionReference) {
        Registry nextRegistry = identifyNextRegistry();
        return populateRegistrySubmitData(submissionReference, nextRegistry);
    }

    JsonNode populateRegistrySubmitData(long submissionReference, Registry registry) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode registryDataObject = mapper.createObjectNode();
        ObjectNode registryMapper = mapper.createObjectNode();

        registryDataObject.put("submissionReference", Long.toString(submissionReference));
        registryMapper.put("name", registry.capitalizeRegistryName());
        registryMapper.put("sequenceNumber", Long.toString(getRegistrySequenceNumber(registry)));
        registryMapper.put("email", registry.getEmail());
        registryMapper.put("address", registry.getAddress());
        registryDataObject.set("registry", registryMapper);

        return registryDataObject;
    }

    JsonNode populateRegistryResubmitData(long submissionReference, JsonNode formDataObject) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode registryDataObject = mapper.createObjectNode();
        ObjectNode registryMapper = mapper.createObjectNode();

        JsonNode formData = formDataObject.get("formdata");
        registryDataObject.put("submissionReference", Long.toString(submissionReference));

        if(formData.has("registry")) {
            registryMapper.set("sequenceNumber", formData.get("registry").get("sequenceNumber"));
            registryMapper.set( "email", formData.get("registry").get("email"));
        } else {
            registryMapper.put("sequenceNumber", String.valueOf(submissionReference));
            registryMapper.put( "email", mailSender.getJavaMailProperties().getProperty("recipient"));
        }

        registryDataObject.set("registry", registryMapper);
        return registryDataObject;
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
