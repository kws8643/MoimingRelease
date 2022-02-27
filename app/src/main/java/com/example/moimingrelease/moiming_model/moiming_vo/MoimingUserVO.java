package com.example.moimingrelease.moiming_model.moiming_vo;

import com.example.moimingrelease.moiming_model.response_dto.MoimingUserResponseDTO;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.annotations.Nullable;

public class MoimingUserVO implements Serializable {

    private UUID uuid;

    @SerializedName("oauth_uid")
    private String oauthUid;

    @SerializedName("oauth_type")
    private String oauthType;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("user_email")
    private String userEmail;

    @Nullable
    @SerializedName("user_pf_img")
    private String userPfImg;

    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("bank_number")
    private String bankNumber;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;

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

    public String getOauthType() {
        return oauthType;
    }

    public void setOauthType(String oauthType) {
        this.oauthType = oauthType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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


    public static MoimingUserVO parseDataToVO(Map<String, Object> userInfo) {


        MoimingUserResponseDTO loginUser = new MoimingUserResponseDTO();

        loginUser.setUuid(UUID.fromString((String) userInfo.get("uuid")));
        loginUser.setOauthUid(((String) userInfo.get("oauth_uid")));
        loginUser.setOauthType(((String) userInfo.get("oauth_type")));
        loginUser.setUserName(((String) userInfo.get("user_name")));
        loginUser.setUserEmail(((String) userInfo.get("user_email")));
        loginUser.setPhoneNumber(((String) userInfo.get("phone_number")));
        loginUser.setUserPfImg(((String) userInfo.get("user_pf_img")));
        loginUser.setBankName(((String) userInfo.get("bank_name")));
        loginUser.setBankNumber(((String) userInfo.get("bank_number")));
        loginUser.setCreatedAt(((String) userInfo.get("created_at")));
        loginUser.setUpdatedAt(((String) userInfo.get("updated_at")));


        return loginUser.convertToVO();
    }


    @Override
    public String toString() {

        return "MoimingUserVO: {\nuuid = " + uuid.toString()
                + "\nuserName= " + userName
                + "\nuserEmail= " + userEmail
                + "\nuserPhoneNumber= " + phoneNumber
                + "\nuserPfImg= " + userPfImg
                + "\nbankName= " + bankName
                + "\nbankNumber= " + bankNumber
                + "\ncreatedAt = " + createdAt
                + "\nupdatedAt = " + updatedAt + "\n}";


    }

}
