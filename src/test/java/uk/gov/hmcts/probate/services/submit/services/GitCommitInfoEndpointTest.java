package uk.gov.hmcts.probate.services.submit.services;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.info.git.location=classpath:uk/gov/hmcts/probate/services/submit/git-test.properties"})
public class GitCommitInfoEndpointTest {

	private static final String EXPECTED_INFO_ENDPOINT_JSON_RESPONSE = "{\"git\":{\"commit\":{\"time\":\"2018-05-23T13:59+1234\",\"id\":\"0773f129ad51c4a23a49fec96fec0888883443f6\"}}}";
	
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mvc;

    @Before
    public void setup() {
        this.context.getBean(MetricsEndpoint.class).setEnabled(true);
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }
    
    @Test
    public void testGitCommitInfoEndpoint()
            throws Exception {
    	MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/info"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();	
    	String actualInfoEndpointJsonResponse = result.getResponse().getContentAsString();   	
    	assertEquals("Test response from info endpoint is correct.",EXPECTED_INFO_ENDPOINT_JSON_RESPONSE, actualInfoEndpointJsonResponse);
    }
}


