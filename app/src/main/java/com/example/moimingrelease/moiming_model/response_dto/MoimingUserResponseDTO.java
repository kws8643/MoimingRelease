package com.example.moimingrelease.moiming_model.response_dto;

import android.util.Log;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class MoimingUserResponseDTO {

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
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public UUID getUuid() {
        return uuid;
    }

    public String getOauthType() {
        return oauthType;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhoneNumber() {
        return phoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserProfileImg() {
        return userPfImg;
    }

    public String getBankName() {
        return bankName;
    }


    public String getBankNumber() {
        return bankNumber;
    }


    public String getCreatedAt() {
        return createdAt;
    }


    public String getUpdatedAt() {
        return updatedAt;
    }


    public MoimingUserVO convertToVO(){

        // 여기서 LocalDateTime 으로 CreatedAt / UpdatedAt 변환 후 VO로 저장.

        MoimingUserVO generateVO = new MoimingUserVO();

        generateVO.setUuid(this.uuid);
        generateVO.setOauthType(this.oauthType);
        generateVO.setUserName(this.userName);
        generateVO.setPhoneNumber(this.phoneNumber);
        generateVO.setUserEmail(this.userEmail);
        generateVO.setUserPfImg(this.userPfImg);
        generateVO.setBankName(this.bankName);
        generateVO.setBankNumber(this.bankNumber);

        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
        generateVO.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        if(this.updatedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
            generateVO.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }

        return generateVO;


    }

}
