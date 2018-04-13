package uk.gov.hmcts.probate.services.submit.clients;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.submit.model.ParsingSubmitException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient implements Client<JsonNode, String> {

    private JavaMailSenderImpl mailSender;
    private MailMessageBuilder mailMessageBuilder;

    @Autowired
    public MailClient(JavaMailSenderImpl mailSender, MailMessageBuilder mailMessageBuilder) {
        this.mailSender = mailSender;
        this.mailMessageBuilder = mailMessageBuilder;
    }

    @Override
    @Retryable(backoff = @Backoff(delay = 100, maxDelay = 500))
    public String execute(JsonNode submitData, long sequenceNumber,  Calendar submissonTimestamp) {
        try {
            MimeMessage message = mailMessageBuilder.buildMessage(submitData, sequenceNumber, mailSender.getJavaMailProperties(), submissonTimestamp);
            mailSender.send(message);
            return String.valueOf(sequenceNumber);
        } catch (MessagingException ex) {
            throw new ParsingSubmitException("Could not build or extract the data from the message", ex);
        }

    }
}
