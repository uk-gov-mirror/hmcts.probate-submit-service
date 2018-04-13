package uk.gov.hmcts.probate.services.submit.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.services.submit.clients.CoreCaseDataClient;
import uk.gov.hmcts.probate.services.submit.clients.MailClient;
import uk.gov.hmcts.probate.services.submit.clients.PersistenceClient;
import static net.logstash.logback.marker.Markers.append;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class SubmitService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DUPLICATE_SUBMISSION = "DUPLICATE_SUBMISSION";
    private MailClient mailClient;
    private PersistenceClient persistenceClient;
    private CoreCaseDataClient coreCaseDataClient;
    @Value("${services.coreCaseData.enabled}")
    private boolean coreCaseDataEnabled;

    @Autowired
    public SubmitService(MailClient mailClient, PersistenceClient persistenceClient, CoreCaseDataClient coreCaseDataClient) {
        this.mailClient = mailClient;
        this.persistenceClient = persistenceClient;
        this.coreCaseDataClient = coreCaseDataClient;
    }

    public String submit(JsonNode submitData, String userId, String authorization) {
        String emailId = submitData.at("/submitdata/applicantEmail").asText();
        JsonNode formData = persistenceClient.loadFormData(emailId);
        if (formData.get("submissionReference").asLong() == 0) {
            String message = "Application submitted, payload version: " +  submitData.at("/submitdata/payloadVersion").asText() + ", number of executors: " + submitData.at("/submitdata/noOfExecutors").asText();
            JsonNode persistenceResponse = persistenceClient.saveSubmission(submitData);
            JsonNode sequenceNumber = persistenceResponse.get("id");
            Calendar submissonTimestamp = Calendar.getInstance();
            mailClient.execute(submitData, sequenceNumber.asLong(), submissonTimestamp);
            logger.info(append("tags","Analytics"), message);
            persistenceClient.updateFormData(emailId, sequenceNumber.asLong(), formData);
            if (coreCaseDataEnabled) {
                try {
                    JsonNode ccdStartCaseResponse = coreCaseDataClient.createCase(userId, authorization);
                    coreCaseDataClient.saveCase(submitData.get("submitdata"), userId, authorization, ccdStartCaseResponse, submissonTimestamp, sequenceNumber);
                } catch (HttpClientErrorException e) {
                    logger.error ("Exception while talking to ccd: ", e);
                    logger.error(e.getMessage());
                    logger.error(e.getResponseBodyAsString());
                } catch (Exception e) {
                    logger.error ("Exception while talking to ccd: ", e);
                    logger.error(e.getMessage());
                }
            }
            return sequenceNumber.asText();
        }
        return new TextNode(DUPLICATE_SUBMISSION).toString();
    }

    public String resubmit(long sequenceId) {
        JsonNode resubmitData = persistenceClient.loadSubmission(sequenceId);
        Calendar submissonTimestamp = Calendar.getInstance();
        return mailClient.execute(resubmitData, sequenceId, submissonTimestamp);
    }
}
