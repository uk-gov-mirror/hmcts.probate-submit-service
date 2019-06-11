package uk.gov.hmcts.probate.services.submit.core.validation;

@FunctionalInterface
public interface Validation<K> {

	ValidationResult test(K param);

}
