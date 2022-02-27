package com.example.moimingrelease.moiming_model.request_dto;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class MoimingGroupRequestDTO {

    @SerializedName("group_name")
    private String groupName;

    @SerializedName("group_info")
    private String groupInfo;

    @SerializedName("group_creator_uuid")
    private UUID groupCreatorUuid;

    @SerializedName("bg_img")
    private String bgImg;

    @SerializedName("group_member_cnt")
    private Integer groupMemberCnt;

    public MoimingGroupRequestDTO(String groupName, String groupInfo, UUID groupCreatorUuid, String bgImg, Integer groupMemberCnt) {

        this.groupName = groupName;
        this.groupInfo = groupInfo;
        this.groupCreatorUuid = groupCreatorUuid;
        this.bgImg = bgImg;
        this.groupMemberCnt = groupMemberCnt;

    }

}
