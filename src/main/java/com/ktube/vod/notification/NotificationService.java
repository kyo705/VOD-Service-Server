package com.ktube.vod.notification;

public interface NotificationService {

    void send(String destination, String subject, String message);
}
