package com.ktube.vod.notification;

public interface NotificationService {

    void send(String destination, String message);

    String createIdentificationMessage(String identificationCode);
}
