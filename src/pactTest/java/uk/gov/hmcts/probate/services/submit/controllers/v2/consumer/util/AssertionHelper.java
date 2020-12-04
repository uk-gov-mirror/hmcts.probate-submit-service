package uk.gov.hmcts.probate.services.submit.controllers.v2.consumer.util;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

public final class AssertionHelper {

    public static void assertCaseDetails(final CaseDetails caseDetails, boolean isWelsh, boolean executorsApplying) {
        assertTrue(caseDetails.getData().size() > 0);
        Map<String,Object> caseDataMap = caseDetails.getData();
        checkCaseData(caseDataMap);

    }

    public static void assertListOfCaseDetails(final List<CaseDetails> caseDetailsList) {

        assertTrue(isNotEmpty(caseDetailsList));
        CaseDetails caseDetails = caseDetailsList.get(0);
        Map<String,Object> caseDataMap = caseDetails.getData();
        checkCaseData(caseDataMap);

    }

    private static void checkCaseData(final Map<String, Object> caseDataMap) {
        assertThat(caseDataMap.get("applicationType"),is("Personal"));
        assertThat(caseDataMap.get("deceasedAddress"),notNullValue());
        assertThat(caseDataMap.get("applicationSubmittedDate"), notNullValue());
        assertThat(caseDataMap.get("primaryApplicantEmailAddress"), notNullValue());
    }
}