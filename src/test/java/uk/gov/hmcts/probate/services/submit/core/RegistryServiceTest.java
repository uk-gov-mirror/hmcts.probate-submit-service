package uk.gov.hmcts.probate.services.submit.core;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.Map;

import static org.junit.Assert.*;

public class RegistryServiceTest {

    private RegistryService registryService;

    @Before
    public void setUp() {
        Map<Integer, Registry> registryMap = ImmutableMap.<Integer, Registry> builder()
            .put(0, Registry.builder()
                .id(1)
                .email("ctsc@ctsc.com")
                .name("ctsc")
                .address("ctsc road")
                .build())
            .put(1, Registry.builder()
                .id(2)
                .email("Oxford@Oxford.com")
                .name("Oxford")
                .address("Oxford road")
                .build())
            .build();
        registryService = new RegistryService(registryMap);
    }

    @Test
    public void shouldGetRegistry(){
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .build();

        registryService.updateRegistry(grantOfRepresentationData);
        assertThat(grantOfRepresentationData.getRegistryLocation(), Matchers.is(RegistryLocation.OXFORD));

        registryService.updateRegistry(grantOfRepresentationData);
        assertThat(grantOfRepresentationData.getRegistryLocation(), Matchers.is(RegistryLocation.CTSC));

        registryService.updateRegistry(grantOfRepresentationData);
        assertThat(grantOfRepresentationData.getRegistryLocation(), Matchers.is(RegistryLocation.OXFORD));
    }
}
