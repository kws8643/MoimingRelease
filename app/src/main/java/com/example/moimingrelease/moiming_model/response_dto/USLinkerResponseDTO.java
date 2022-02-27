package com.example.moimingrelease.moiming_model.response_dto;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import androidx.browser.browseractions.BrowserActionsIntent;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.UserSessionLinkerVO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class USLinkerResponseDTO {

    @SerializedName("personal_cost")
    private Integer personalCost;

    @SerializedName("is_sent")
    private boolean isSent;

    // 연결되어 있는 모이밍 유저를 전달
    @SerializedName("moiming_user")
    private MoimingUserResponseDTO moimingUserResponseDTO;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @Override
    public String toString() {
        return "USLinkerResponseDTO: {"
                + "\npersonalCost= " + personalCost
                + "\nisSent= " + String.valueOf(isSent)
                + "\ncreatedAt= " + createdAt
                + "\nmoimingUserResponseDTO= " + moimingUserResponseDTO.getUserName()
                + "\nmoimingUserResponseDTO= " + moimingUserResponseDTO.getUserEmail()
                + "\n}";

    }

    public UserSessionLinkerVO convertToVO() {

        // 여기서 LocalDateTime 으로 CreatedAt / UpdatedAt 변환 후 VO로 저장.
        UserSessionLinkerVO generateVO = new UserSessionLinkerVO();

        generateVO.setPersonalCost(this.personalCost);
        generateVO.setSent(this.isSent);
        generateVO.setMoimingUser(this.moimingUserResponseDTO.convertToVO());

        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        generateVO.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        if (this.updatedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            generateVO.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }

        return generateVO;
    }
}
