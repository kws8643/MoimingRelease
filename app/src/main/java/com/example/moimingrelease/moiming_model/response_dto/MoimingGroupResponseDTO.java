package com.example.moimingrelease.moiming_model.response_dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class MoimingGroupResponseDTO implements Parcelable {

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


    public static final Creator<MoimingGroupResponseDTO> CREATOR = new Creator<MoimingGroupResponseDTO>() {
        @Override
        public MoimingGroupResponseDTO createFromParcel(Parcel in) {
            return new MoimingGroupResponseDTO(in);
        }

        @Override
        public MoimingGroupResponseDTO[] newArray(int size) {
            return new MoimingGroupResponseDTO[size];
        }
    };

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


        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        moimingGroupVO.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        if(this.updatedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            moimingGroupVO.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }

        return moimingGroupVO;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid.toString());
        dest.writeString(groupName);
        dest.writeString(groupInfo);
        dest.writeString(groupPfImg);
        dest.writeString(bgImg);
        dest.writeInt(groupMemberCnt);
        dest.writeString(groupCreatorUuid.toString());
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    public MoimingGroupResponseDTO(Parcel in) {
        uuid = UUID.fromString(in.readString());
        groupName = in.readString();
        groupInfo = in.readString();
        groupPfImg = in.readString();
        bgImg = in.readString();
        groupMemberCnt = in.readInt();
        groupCreatorUuid = UUID.fromString(in.readString());
        createdAt = in.readString();
        updatedAt = in.readString();
    }

}
