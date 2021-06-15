package com.example.moimingrelease.moiming_model.moiming_vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.UUID;

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


    @Override
    public String toString(){

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
