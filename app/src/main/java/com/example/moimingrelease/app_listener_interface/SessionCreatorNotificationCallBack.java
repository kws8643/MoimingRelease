package com.example.moimingrelease.app_listener_interface;

import java.util.UUID;

public interface SessionCreatorNotificationCallBack {

    void changingUserState(UUID userUuid, boolean isChecking, boolean isNotification);

    void deleteNotification(UUID sentUserUuid);

    void sendFinishRequest(UUID memberUuid, int personalCost);

}
