package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor
public class InvitationElasticSearchQueryBuilder {

    String buildQuery(String invitationId) {
        String searchString = "{\"query\":{\"match_phrase\":{ \"data.executorsApplying.value.applyingExecutorInvitiationId\":\" " +
                invitationId +
                "\"}}}";
        return searchString;
    }
}
