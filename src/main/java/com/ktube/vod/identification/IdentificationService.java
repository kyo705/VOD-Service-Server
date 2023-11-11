package com.ktube.vod.identification;

import com.ktube.vod.notification.NotificationFailureException;
import com.ktube.vod.user.KTubeUser;

public interface IdentificationService {

    String createIdentification(KTubeUser user) throws NotificationFailureException;

    KTubeUser identify(String identificationCode) throws IdentificationFailureException;
}
