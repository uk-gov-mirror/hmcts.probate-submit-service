package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;

import org.hamcrest.*;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvitationElasticSearchQueryBuilderTest {


    InvitationElasticSearchQueryBuilder invitationElasticSearchQueryBuilder = new InvitationElasticSearchQueryBuilder();

    @Test
    public void shouldBuildQuery(){
        String result = invitationElasticSearchQueryBuilder.buildQuery("123456");
        Assert.assertThat(result, Matchers.equalTo("{\"query\":{\"match_phrase\":" +
                "{ \"data.executorsApplying.value.applyingExecutorInvitiationId\":\" 123456\"}}}"));

    }
}