package com.example.moimingrelease.moiming_model.extras;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class SessionStatusChangeDTO {

    @SerializedName("session_uuid")
    private UUID sessionUuid;

    @SerializedName("unsent_user_list")
    private List<UUID> unsentUserList;

    @SerializedName("unsent_nmu_list")
    private List<UUID> unsentNmuList;

    @SerializedName("sent_user_list")
    private List<UUID> sentUserList;

    @SerializedName("sent_nmu_list")
    private List<UUID> sentNmuList;

    public SessionStatusChangeDTO(UUID sessionUuid, List<UUID> unsentUserList, List<UUID> unsentNmuList, List<UUID> sentUserList, List<UUID> sentNmuList) {

        this.sessionUuid = sessionUuid;
        this.unsentUserList = unsentUserList;
        this.unsentNmuList = unsentNmuList;
        this.sentUserList = sentUserList;
        this.sentNmuList = sentNmuList;

    }

    public UUID getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(UUID sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    public List<UUID> getUnsentUserList() {
        return unsentUserList;
    }

    public void setUnsentUserList(List<UUID> unsentUserList) {
        this.unsentUserList = unsentUserList;
    }

    public List<UUID> getUnsentNmuList() {
        return unsentNmuList;
    }

    public void setUnsentNmuList(List<UUID> unsentNmuList) {
        this.unsentNmuList = unsentNmuList;
    }

    public List<UUID> getSentUserList() {
        return sentUserList;
    }

    public void setSentUserList(List<UUID> sentUserList) {
        this.sentUserList = sentUserList;
    }

    public List<UUID> getSentNmuList() {
        return sentNmuList;
    }

    public void setSentNmuList(List<UUID> sentNmuList) {
        this.sentNmuList = sentNmuList;
    }
}
