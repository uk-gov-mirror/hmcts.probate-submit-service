package uk.gov.hmcts.probate.services.submit.clients;

import java.util.Calendar;

@FunctionalInterface
interface Client<T, R> {
    R execute(T t, long sequenceNumber, Calendar submissonTimestamp);
}
