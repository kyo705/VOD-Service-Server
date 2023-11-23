package com.ktube.vod.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@Qualifier("emailNotificationService")
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;

    @Async("threadPoolTaskExecutor")
    @Override
    public void send(String destination, String message) {

        String subject = NotificationConstants.IDENTIFICATION_EMAIL_SUBJECT;

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(destination);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        try {
            mailSender.send(simpleMailMessage);
            log.info("메일 전송");
        }
        catch (MailException e) {
            throw new NotificationFailureException();
        }
    }
}
