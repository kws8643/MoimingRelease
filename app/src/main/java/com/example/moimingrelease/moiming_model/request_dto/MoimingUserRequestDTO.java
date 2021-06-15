package com.example.moimingrelease.moiming_model.request_dto;
// Test Create Model

import com.google.gson.annotations.SerializedName;

// Create USER
public class MoimingUserRequestDTO {


    @SerializedName("oauth_uid")
    private String oauthUid;

    @SerializedName("oauth_type")
    private String oauthType;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_email")
    private String userEmail;

    @SerializedName("user_pf_img")
    private String userPfImg;

    @SerializedName("phone_number")
    private String phoneNumber;

    public MoimingUserRequestDTO(String oauthUid, String oauthType, String userName, String userEmail, String userPfImg, String userPhoneNumber) {

        this.oauthUid = oauthUid;
        this.oauthType = oauthType;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPfImg = userPfImg;
        this.phoneNumber = userPhoneNumber;

    }

    @Override
    public String toString() {

        return "MoimingUserRequestDTO: {\noauth_uid = " + oauthUid
                + "\noauthType = " + oauthType
                + "\nuserName= " + userName
                + "\nuserEmail= " + userEmail
                + "\nuserPhoneNumber= " + phoneNumber
                + "\n}";

    }

}
