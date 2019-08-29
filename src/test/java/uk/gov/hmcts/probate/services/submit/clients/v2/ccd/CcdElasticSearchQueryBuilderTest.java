package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;

import org.hamcrest.*;
import org.junit.Assert;
import org.junit.Test;

public class CcdElasticSearchQueryBuilderTest {


    CcdElasticSearchQueryBuilder ccdElasticSearchQueryBuilder = new CcdElasticSearchQueryBuilder();

    @Test
    public void shouldBuildQuery(){
        String result = ccdElasticSearchQueryBuilder.buildQuery("123456", "executorsApplying.value.applyingExecutorInvitationId");
        Assert.assertThat(result, Matchers.equalTo("{\"query\":{\"bool\":{\"filter\":{\"term\":{\"data.executorsApplying.value.applyingExecutorInvitationId.keyword\":\"123456\"}}}}}"));

    }
}