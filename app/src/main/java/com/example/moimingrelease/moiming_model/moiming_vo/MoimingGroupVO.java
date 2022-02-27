package com.example.moimingrelease.moiming_model.moiming_vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class MoimingGroupVO implements Serializable, Parcelable {

    @SerializedName("uuid")
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
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;

    public MoimingGroupVO() {

    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }

    public String getGroupPfImg() {
        return groupPfImg;
    }

    public void setGroupPfImg(String groupPfImg) {
        this.groupPfImg = groupPfImg;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public Integer getGroupMemberCnt() {
        return groupMemberCnt;
    }

    public void setGroupMemberCnt(Integer groupMemberCnt) {
        this.groupMemberCnt = groupMemberCnt;
    }

    public UUID getGroupCreatorUuid() {
        return groupCreatorUuid;
    }

    public void setGroupCreatorUuid(UUID groupCreatorUuid) {
        this.groupCreatorUuid = groupCreatorUuid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {

        return "MoimingGroupVO: {\nuuid = " + uuid.toString()
                + "\ngroupName= " + groupName
                + "\ngroupInfo= " + groupInfo
                + "\ngroupPfImg= " + groupPfImg
                + "\nbgImg= " + bgImg
                + "\ngroupMemberCnt= " + String.valueOf(groupMemberCnt)
                + "\ngroupCreatorUuid= " + groupCreatorUuid.toString()
                + "\ncreatedAt = " + createdAt
                + "\nupdatedAt = " + updatedAt + "\n}";


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
        dest.writeSerializable(createdAt);
        dest.writeSerializable(updatedAt);
    }

    public MoimingGroupVO(Parcel in) {

        uuid = UUID.fromString(in.readString());
        groupName = in.readString();
        groupInfo = in.readString();
        groupPfImg = in.readString();
        bgImg = in.readString();
        groupMemberCnt = in.readInt();
        groupCreatorUuid = UUID.fromString(in.readString());
        createdAt = (LocalDateTime) in.readSerializable();
        updatedAt = (LocalDateTime) in.readSerializable();

    }

    public static final Creator<MoimingGroupVO> CREATOR = new Creator<MoimingGroupVO>() {
        @Override
        public MoimingGroupVO createFromParcel(Parcel in) {
            return new MoimingGroupVO(in);
        }

        @Override
        public MoimingGroupVO[] newArray(int size) {
            return new MoimingGroupVO[size];
        }
    };

}
