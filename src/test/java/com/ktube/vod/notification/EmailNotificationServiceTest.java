package com.ktube.vod.notification;


import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class EmailNotificationServiceTest {

    @RegisterExtension
    static GreenMailExtension GREEN_MAIL = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @Autowired
    private NotificationService emailNotificationService;

    @Test
    public void testSendMessage() {

        //given
        String destinationEmail = "destinationEmail@naver.com";
        String subject = "subject";
        String message = "message";

        //when
        emailNotificationService.send(destinationEmail, subject, message);

        //then
        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilAsserted(()-> {
            MimeMessage receivedMessage = GREEN_MAIL.getReceivedMessages()[0];

            assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(destinationEmail);
            assertThat(receivedMessage.getSubject()).isEqualTo(subject);
            assertThat(GreenMailUtil.getBody(receivedMessage)).isEqualTo(message);
            assertThat(receivedMessage.getFrom()[0].toString()).isEqualTo(NotificationConstants.IDENTIFICATION_EMAIL_SENDER);
        });
    }

}
