package uk.gov.hmcts.probate.services.submit.model.v2.validation;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;
import uk.gov.hmcts.probate.services.submit.model.v2.CaseData;
import uk.gov.hmcts.probate.services.submit.model.v2.grantofrepresentation.GrantOfRepresentation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class AtLeastOneNonEmptyFieldValidatorTest {

    @Test
    public void test(){
        CaseData caseData = GrantOfRepresentation.builder().primaryApplicantEmailAddress("test@test").build();

        Arrays.stream(caseData.getClass().getDeclaredFields())
                .map(Field::getName)
                .filter(name -> isEmpty(name, caseData))
                .count();

    }

    private boolean isEmpty(String name, CaseData caseData) {
        try {
            Object obj = MethodUtils.invokeMethod(caseData, "get" + StringUtils.capitalize(name));
            if (obj instanceof String) {
                return StringUtils.isBlank((String) obj);
            }
           return obj == null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }
}