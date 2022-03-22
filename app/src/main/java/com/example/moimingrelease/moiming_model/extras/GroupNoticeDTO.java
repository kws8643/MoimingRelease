package com.example.moimingrelease.moiming_model.extras;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class GroupNoticeDTO {

    @SerializedName("group_uuid")
    private UUID groupUuid;

    @SerializedName("notice_creator_uuid")
    private UUID noticeCreatorUuid;

    @SerializedName("notice_info")
    private String noticeInfo;


    public GroupNoticeDTO(UUID groupUuid, UUID noticeCreatorUuid, String noticeInfo) {

        this.groupUuid = groupUuid;

        this.noticeCreatorUuid = noticeCreatorUuid;

        this.noticeInfo = noticeInfo;

    }

    public UUID getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(UUID groupUuid) {
        this.groupUuid = groupUuid;
    }

    public UUID getNoticeCreatorUuid() {
        return noticeCreatorUuid;
    }

    public void setNoticeCreatorUuid(UUID noticeCreatorUuid) {
        this.noticeCreatorUuid = noticeCreatorUuid;
    }

    public String getNoticeInfo() {
        return noticeInfo;
    }

    public void setNoticeInfo(String noticeInfo) {
        this.noticeInfo = noticeInfo;
    }
}
