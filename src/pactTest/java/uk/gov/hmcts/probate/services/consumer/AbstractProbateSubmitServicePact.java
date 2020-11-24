package uk.gov.hmcts.probate.services.consumer;

import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.annotations.PactFolder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//@ExtendWith(PactConsumerTestExt.class)
//@ExtendWith(SpringExtension.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@PactTestFor(providerName = "ccd", port = "8893")
//@PactFolder("pacts")
//@SpringBootTest({
//    "core_case_data.api.url : localhost:8893"
//})
public abstract class AbstractProbateSubmitServicePact {


}
