package uk.gov.hmcts.probate.services.submit.core;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.services.submit.Registry;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RegistryServiceTest {

    private static final String CTSC = "ctsc";
    private static final String CTSC_EMAIL = "ctsc@ctsc.com";
    private static final String CTSC_ADDRESS = "ctsc road";
    private static final long CTSC_ID = 1L;
    private static final Registry.RegistryBuilder OXFORD_ID = Registry.builder()
        .id(2L);
    private static final String OXFORD_EMAIL = "Oxford@Oxford.com";
    private static final String OXFORD = "Oxford";
    private static final String OXFORD_ADDRESS = "Oxford road";
    private static final long ID = 2L;
    private RegistryService registryService;

    @Before
    public void setUp() {
        Map<Integer, Registry> registryMap = ImmutableMap.<Integer, Registry>builder()
            .put(0, Registry.builder()
                .id(CTSC_ID)
                .email(CTSC_EMAIL)
                .name(CTSC)
                .address(CTSC_ADDRESS)
                .build())
            .put(1, Registry.builder()
                .id(ID)
                .email(OXFORD_EMAIL)
                .name(OXFORD)
                .address(OXFORD_ADDRESS)
                .build())
            .build();
        registryService = new RegistryService(registryMap);
    }

    @Test
    public void shouldGetRegistry() {
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

    @Test
    public void shouldUpdateCaveatRegistry() {
        CaveatData caveatData = CaveatData.builder()
            .build();
        registryService.updateRegistry(caveatData);

        assertThat(caveatData.getRegistryLocation(), is(RegistryLocation.OXFORD));
    }
}
