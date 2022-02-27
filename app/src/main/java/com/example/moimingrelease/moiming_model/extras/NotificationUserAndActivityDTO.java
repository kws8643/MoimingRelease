package com.example.moimingrelease.moiming_model.extras;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;


public class NotificationUserAndActivityDTO {

    @SerializedName("user_uuid")
    private UUID userUuid;

    @SerializedName("sent_user_uuid")
    private UUID sentUserUuid;

    @SerializedName("activity_uuid")
    private UUID activityUuid;

    public NotificationUserAndActivityDTO(UUID userUuid, UUID sentUserUuid, UUID activityUuid) {

        this.userUuid = userUuid;
        this.sentUserUuid = sentUserUuid;
        this.activityUuid = activityUuid;
    }



}
