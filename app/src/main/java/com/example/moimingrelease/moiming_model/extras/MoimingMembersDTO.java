package com.example.moimingrelease.moiming_model.extras;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import io.reactivex.rxjava3.annotations.Nullable;

public class MoimingMembersDTO implements Parcelable { // DB에 VO까지 없고, 그냥 DTO일 뿐.

    @SerializedName("uuid")
    private UUID uuid;

    @SerializedName("oauth_uid")
    private String oauthUid;

    @SerializedName("user_name")
    private String userName;

    @Nullable
    @SerializedName("user_pf_img")
    private String userPfImg;

    @Nullable
    @SerializedName("bank_name")
    private String bankName;

    @Nullable
    @SerializedName("bank_number")
    private String bankNumber;

    public MoimingMembersDTO(){}


    public MoimingMembersDTO(UUID uuid, String userName, String userPfImg) {

        this.uuid = uuid;
        this.userName = userName;
        this.userPfImg = userPfImg;

    }

    public String getOauthUid() {
        return oauthUid;
    }

    public void setOauthUid(String oauthUid) {
        this.oauthUid = oauthUid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserPfImg() {
        return userPfImg;
    }


    public void setUserPfImg(String userPfImg) {
        this.userPfImg = userPfImg;
    }


    public String getBankName() {
        return bankName;
    }


    public void setBankName(String bankName) {
        this.bankName = bankName;
    }


    public String getBankNumber() {
        return bankNumber;
    }


    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
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
    }

    public static final Creator<MoimingMembersDTO> CREATOR = new Creator<MoimingMembersDTO>() {
        @Override
        public MoimingMembersDTO createFromParcel(Parcel in) {
            return new MoimingMembersDTO(in);
        }

        @Override
        public MoimingMembersDTO[] newArray(int size) {
            return new MoimingMembersDTO[size];
        }
    };


    public String toString() {

        String result =
                "uuid: " + getUuid().toString()
                        + "\nname: " + getUserName();


        return result;
    }
}
