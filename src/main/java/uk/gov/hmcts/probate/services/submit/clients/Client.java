package uk.gov.hmcts.probate.services.submit.clients;

import java.util.Calendar;
import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
interface Client<T, R> {
    R execute(T t, JsonNode sequenceNumber, Calendar submissionTimestamp);
}
