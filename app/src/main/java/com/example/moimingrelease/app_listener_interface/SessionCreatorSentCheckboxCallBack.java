package com.example.moimingrelease.app_listener_interface;

import java.util.UUID;

public interface SessionCreatorSentCheckboxCallBack {

    void changeSentUserState(UUID userUuid, boolean isChecking, boolean isMoimingUser);

}
