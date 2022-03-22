package com.example.moimingrelease.moiming_model.moiming_vo;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationVO {

    private UUID sentUserUuid;

    private String sentActivity;

    private UUID sentGroupUuid;

    private UUID sentSessionUuid;

    private Integer msgType;

    private String msgText; // 이게 Received Notification DTO 에 있는 Full Text 포함.

    private Boolean isRead;

    private LocalDateTime createdAt;

    public UUID getSentUserUuid() {
        return sentUserUuid;
    }

    public void setSentUserUuid(UUID sentUserUuid) {
        this.sentUserUuid = sentUserUuid;
    }

    public String getSentActivity() {
        return sentActivity;
    }

    public void setSentActivity(String sentActivity) {
        this.sentActivity = sentActivity;
    }

    public UUID getSentGroupUuid() {
        return sentGroupUuid;
    }

    public void setSentGroupUuid(UUID sentGroupUuid) {
        this.sentGroupUuid = sentGroupUuid;
    }

    public UUID getSentSessionUuid() {
        return sentSessionUuid;
    }

    public void setSentSessionUuid(UUID sentSessionUuid) {
        this.sentSessionUuid = sentSessionUuid;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
