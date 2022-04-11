package com.example.moimingrelease.moiming_model.request_dto;

import com.google.auto.value.extension.serializable.SerializableAutoValue;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationRequestDTO {

    @SerializedName("to_user_uuid")
    private UUID toUserUuid;

    @SerializedName("sent_user_uuid")
    private UUID sentUserUuid;

    @SerializedName("sent_activity")
    private String sentActivity;

    @SerializedName("sent_group_uuid")
    private UUID sentGroupUuid;

    @SerializedName("sent_session_uuid")
    private UUID sentSessionUuid;

    @SerializedName("msg_type")
    private Integer msgType;

    @SerializedName("msg_text")
    private String msgText;


    public NotificationRequestDTO(UUID toUserUuid, UUID sentUserUuid, String sentActivity
            , UUID sentGroupUuid, UUID sentSessionUuid, Integer msgType, String msgText) {

        this.toUserUuid = toUserUuid;
        this.sentUserUuid = sentUserUuid;
        this.sentActivity = sentActivity;
        this.sentGroupUuid = sentGroupUuid;
        this.sentSessionUuid = sentSessionUuid;
        this.msgText = msgText;
        this.msgType = msgType;

    }

}
