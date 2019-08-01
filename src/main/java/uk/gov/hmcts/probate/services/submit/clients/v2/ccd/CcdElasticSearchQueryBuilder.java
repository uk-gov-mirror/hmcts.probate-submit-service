package uk.gov.hmcts.probate.services.submit.clients.v2.ccd;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor
public class CcdElasticSearchQueryBuilder {

    String buildQuery(String searchValue, String searchField) {
        return "{\"query\":{\"term\":{ \"data."
            + searchField
            + ".keyword\":\"" + searchValue + "\"}}}";
    }

}
