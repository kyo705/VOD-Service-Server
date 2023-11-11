package com.ktube.vod.notification;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
@Qualifier("emailNotificationService")
public class EmailNotificationService implements NotificationService {

    @Override
    public void send(String destination, String message) {

    }

    @Override
    public String createIdentificationMessage(String identificationCode) {
        return null;
    }
}
