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
    private static final String SUBMISSION_REFERENCE = "submissionReference";
    private static final String REGISTRY = "registry";
    private static final String SEQUENCE_NUMBER = "sequenceNumber";
    private static final String EMAIL = "email";

    private final Map<Integer, Registry> registryMap;
    private final PersistenceClient persistenceClient;
    private final JavaMailSenderImpl mailSender;
    private final ObjectMapper mapper;

    private static int registryCounter = 1;

    @Autowired
    public SequenceService(Map<Integer, Registry> registryMap, PersistenceClient persistenceClient, JavaMailSenderImpl mailSender, ObjectMapper mapper) {
        this.registryMap = registryMap;
        this.persistenceClient = persistenceClient;
        this.mailSender = mailSender;
        this.mapper = mapper;
    }

    public synchronized JsonNode nextRegistry(long submissionReference) {
        Registry nextRegistry = identifyNextRegistry();
        return populateRegistrySubmitData(submissionReference, nextRegistry);
    }

    JsonNode populateRegistrySubmitData(long submissionReference, Registry registry) {
        ObjectNode registryDataObject = mapper.createObjectNode();
        ObjectNode registryMapper = mapper.createObjectNode();

        registryDataObject.put(SUBMISSION_REFERENCE, submissionReference);
        registryMapper.put("name", registry.capitalizeRegistryName());
        registryMapper.put(SEQUENCE_NUMBER, getRegistrySequenceNumber(registry));
        registryMapper.put(EMAIL, registry.getEmail());
        registryMapper.put("address", registry.getAddress());
        registryDataObject.set(REGISTRY, registryMapper);

        return registryDataObject;
    }

    JsonNode populateRegistryResubmitData(long submissionReference, JsonNode formDataObject) {
        ObjectNode registryDataObject = mapper.createObjectNode();
        ObjectNode registryMapper = mapper.createObjectNode();

        JsonNode formData = formDataObject.get("formdata");
        registryDataObject.put(SUBMISSION_REFERENCE, submissionReference);

        if(formData.has(REGISTRY)) {
            registryMapper.set(SEQUENCE_NUMBER, formData.get(REGISTRY).get(SEQUENCE_NUMBER));
            registryMapper.set( EMAIL, formData.get(REGISTRY).get(EMAIL));
        } else {
            registryMapper.put(SEQUENCE_NUMBER, submissionReference);
            registryMapper.put( EMAIL, mailSender.getJavaMailProperties().getProperty("recipient"));
        }

        registryDataObject.set(REGISTRY, registryMapper);
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
