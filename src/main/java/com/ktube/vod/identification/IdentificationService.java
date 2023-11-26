package com.ktube.vod.identification;

import com.ktube.vod.notification.NotificationFailureException;

public interface IdentificationService {

    String createIdentification(String destination, Object data) throws NotificationFailureException;

    Object identify(String identificationCode) throws IdentificationFailureException;
}
