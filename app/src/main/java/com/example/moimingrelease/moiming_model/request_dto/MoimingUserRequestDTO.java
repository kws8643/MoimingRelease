package com.example.moimingrelease.moiming_model.request_dto;
// Test Create Model

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

// Create USER
public class MoimingUserRequestDTO {

    @SerializedName("user_uuid")
    private UUID userUuid;

    @SerializedName("oauth_uid")
    private String oauthUid;

    @SerializedName("oauth_type")
    private String oauthType;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_email")
    private String userEmail;

    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("bank_number")
    private String bankNumber;

    @SerializedName("user_pf_img")
    private String userPfImg;

    @SerializedName("phone_number")
    private String phoneNumber;

    public MoimingUserRequestDTO(UUID userUuid, String oauthUid, String oauthType, String userName, String userEmail
            , String bankName, String bankNumber, String userPfImg, String userPhoneNumber) {

        this.userUuid = userUuid;
        this.oauthUid = oauthUid;
        this.oauthType = oauthType;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPfImg = userPfImg;
        this.phoneNumber = userPhoneNumber;
        this.bankName = bankName;
        this.bankNumber = bankNumber;

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
