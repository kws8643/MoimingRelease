package com.example.moimingrelease.moiming_model.response_dto;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class MoimingGroupResponseDTO {

    private UUID uuid;

    @SerializedName("group_name")
    private String groupName;

    @SerializedName("group_info")
    private String groupInfo;

    @SerializedName("group_pf_img")
    private String groupPfImg;

    @SerializedName("bg_img")
    private String bgImg;

    @SerializedName("group_member_cnt")
    private Integer groupMemberCnt;

    @SerializedName("group_creator_uuid")
    private UUID groupCreatorUuid;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public String getGroupName(){

        return this.groupName;

    }

    public MoimingGroupVO convertToVO() {

        MoimingGroupVO moimingGroupVO = new MoimingGroupVO();

        moimingGroupVO.setUuid(this.uuid);
        moimingGroupVO.setGroupName(this.groupName);
        moimingGroupVO.setGroupInfo(this.groupInfo);
        moimingGroupVO.setGroupPfImg(this.groupPfImg);
        moimingGroupVO.setBgImg(this.bgImg);
        moimingGroupVO.setGroupMemberCnt(this.groupMemberCnt);
        moimingGroupVO.setGroupCreatorUuid(this.groupCreatorUuid);


        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
        moimingGroupVO.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        if(this.updatedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
            moimingGroupVO.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }

        return moimingGroupVO;
    }


}
