package com.example.moimingrelease.moiming_model.request_dto;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class MoimingGroupEditInfoDto {

    @SerializedName("group_uuid")
    private UUID groupUuid;

    @SerializedName("group_name")
    private String groupName;

    @SerializedName("group_info")
    private String groupInfo;

    @SerializedName("group_pf_img")
    private String groupPfImg;

    public MoimingGroupEditInfoDto(UUID groupUuid, String groupName, String groupInfo, String groupPfImg){

        this.groupUuid = groupUuid;
        this.groupInfo = groupInfo;
        this.groupName = groupName;
        this.groupPfImg = groupPfImg;

    }


}
