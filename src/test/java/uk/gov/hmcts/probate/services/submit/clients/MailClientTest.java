package uk.gov.hmcts.probate.services.submit.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;
import uk.gov.hmcts.probate.services.submit.utils.TestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MailClientTest {

    @Autowired
    SpringTemplateEngine templateEngine;
    @Mock
    private MailMessageBuilder mailMessageBuilderMock;
    private MailClient mailClient;
    @Mock
    private RestTemplate restTemplateMock;
    @Mock
    private JavaMailSenderImpl mailSenderMock;
    @Mock
    private MailProperties mailPropertiesMock;
    @Mock
    private MimeMessage mimeMessageMock;
        
    private Calendar submissonTimestamp;
    private JsonNode registryData;
    private TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mailClient = new MailClient(mailSenderMock, mailMessageBuilderMock);
        submissonTimestamp = Calendar.getInstance();
        testUtils = new TestUtils();
        registryData = testUtils.getJsonNodeFromFile("registryDataSubmit.json");
    }
    

        @Test
    public void testProcessSuccess() throws MessagingException {
        doNothing().when(mailSenderMock).send(any(MimeMessage.class));
        when(mailMessageBuilderMock.buildMessage(any(JsonNode.class), any(JsonNode.class), any(Properties.class), any(Calendar.class))).thenReturn(mimeMessageMock);
        when(mimeMessageMock.getHeader(anyString(),any())).thenReturn("1234");

        String response = mailClient.execute(NullNode.getInstance(), registryData, submissonTimestamp);

        assertThat(response, is("1234"));
    }

}