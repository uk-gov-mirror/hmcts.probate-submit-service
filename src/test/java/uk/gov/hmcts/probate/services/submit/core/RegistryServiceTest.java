package uk.gov.hmcts.probate.services.submit.core;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class RegistryServiceTest {

    public static final String CTSC = "ctsc";
    public static final String CTSC_EMAIL = "ctsc@ctsc.com";
    public static final String CTSC_ADDRESS = "ctsc road";
    public static final long CTSC_ID = 1L;
    public static final Registry.RegistryBuilder OXFORD_ID = Registry.builder()
        .id(2L);
    public static final String OXFORD_EMAIL = "Oxford@Oxford.com";
    public static final String OXFORD = "Oxford";
    public static final String OXFORD_ADDRESS = "Oxford road";
    private RegistryService registryService;

    @Before
    public void setUp() {
        Map<Integer, Registry> registryMap = ImmutableMap.<Integer, Registry> builder()
            .put(0, Registry.builder()
                .id(CTSC_ID)
                .email(CTSC_EMAIL)
                .name(CTSC)
                .address(CTSC_ADDRESS)
                .build())
            .put(1, Registry.builder()
                .email(OXFORD_EMAIL)
                .name(OXFORD)
                .address(OXFORD_ADDRESS)
                .build())
            .build();
        registryService = new RegistryService(registryMap);
    }

    @Test
    public void shouldGetRegistry(){
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .build();

        registryService.updateRegistry(grantOfRepresentationData);
        assertThat(grantOfRepresentationData.getRegistryLocation(), is(RegistryLocation.OXFORD));
        assertThat(grantOfRepresentationData.getRegistryAddress(), is(OXFORD_ADDRESS));
        assertThat(grantOfRepresentationData.getRegistryEmailAddress(), is(OXFORD_EMAIL));

        registryService.updateRegistry(grantOfRepresentationData);
        assertThat(grantOfRepresentationData.getRegistryLocation(), is(RegistryLocation.CTSC));
        assertThat(grantOfRepresentationData.getRegistryAddress(), is(CTSC_ADDRESS));
        assertThat(grantOfRepresentationData.getRegistryEmailAddress(), is(CTSC_EMAIL));

        registryService.updateRegistry(grantOfRepresentationData);
        assertThat(grantOfRepresentationData.getRegistryLocation(), is(RegistryLocation.OXFORD));
        assertThat(grantOfRepresentationData.getRegistryAddress(), is(OXFORD_ADDRESS));
        assertThat(grantOfRepresentationData.getRegistryEmailAddress(), is(OXFORD_EMAIL));
    }
}
