package com.example.moimingrelease.moiming_model.extras;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;
import com.google.auto.value.extension.serializable.SerializableAutoValue;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class MoimingGroupAndMembersDTO implements Parcelable {

    @SerializedName("moiming_group")
    private MoimingGroupVO moimingGroup;

    @SerializedName("moiming_group_dto") // Refresh 반환용
    private MoimingGroupResponseDTO moimingGroupDto;

    @SerializedName("moiming_members_list")
    private List<MoimingMembersDTO> moimingMembersList; // curUser도 포함되어 있음!

    public MoimingGroupAndMembersDTO(MoimingGroupVO moimingGroup, List<MoimingMembersDTO> moimingMembersList) {

        this.moimingGroup = moimingGroup;
        this.moimingMembersList = moimingMembersList;

    }

    public MoimingGroupResponseDTO getMoimingGroupDto() {
        return moimingGroupDto;
    }

    public void setMoimingGroupDto(MoimingGroupResponseDTO moimingGroupDto) {
        this.moimingGroupDto = moimingGroupDto;
    }


    public MoimingGroupVO getMoimingGroup() {
        return moimingGroup;
    }

    public void setMoimingGroup(MoimingGroupVO moimingGroup) {
        this.moimingGroup = moimingGroup;
    }

    public List<MoimingMembersDTO> getMoimingMembersList() {
        return moimingMembersList;
    }

    public void setMoimingMembersList(List<MoimingMembersDTO> moimingMembersList) {
        this.moimingMembersList = moimingMembersList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

  /*  @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid.toString());
        dest.writeString(oauthUid);
        dest.writeString(userName);
        dest.writeString(userPfImg);
        dest.writeString(bankName);
        dest.writeString(bankNumber);
    }

    protected MoimingMembersDTO(Parcel in) {
        uuid = UUID.fromString(in.readString());
        oauthUid = in.readString();
        userName = in.readString();
        userPfImg = in.readString();
        bankName = in.readString();
        bankNumber = in.readString();
    }*/


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(moimingGroup, flags);
        dest.writeParcelable(moimingGroupDto, flags);
        dest.writeTypedList(moimingMembersList);
    }

    protected MoimingGroupAndMembersDTO(Parcel in) {
        moimingGroup = in.readParcelable(MoimingGroupVO.class.getClassLoader());
        moimingGroupDto = in.readParcelable(MoimingGroupResponseDTO.class.getClassLoader());
        moimingMembersList = in.createTypedArrayList(MoimingMembersDTO.CREATOR);
    }

    public static final Creator<MoimingGroupAndMembersDTO> CREATOR = new Creator<MoimingGroupAndMembersDTO>() {
        @Override
        public MoimingGroupAndMembersDTO createFromParcel(Parcel in) {
            return new MoimingGroupAndMembersDTO(in);
        }

        @Override
        public MoimingGroupAndMembersDTO[] newArray(int size) {
            return new MoimingGroupAndMembersDTO[size];
        }
    };
}
