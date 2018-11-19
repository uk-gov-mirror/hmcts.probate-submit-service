package uk.gov.hmcts.probate.services.submit.model.v2;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.services.submit.model.v2.grantofrepresentation.GrantOfRepresentation;

@RunWith(SpringRunner.class)
@JsonTest
public class GrantOfRepresentationJsonTest {

    @Autowired
    private JacksonTester<GrantOfRepresentation> jacksonTester;

    @Test
    public void shouldDeserialize() throws Exception {

        ObjectContent<GrantOfRepresentation> gop = jacksonTester.parse(IOUtils.toString(this.getClass().getResource("success.pa.ccd.json")));
        gop.assertThat().hasFieldOrProperty("ss");
    }

}