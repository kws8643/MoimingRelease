package com.example.moimingrelease.moiming_model.response_dto;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class NonMoimingUserResponseDTO {

    private UUID uuid;

    @SerializedName("nmu_name")
    private String nmuName;

    @SerializedName("nmu_personal_cost")
    private Integer nmuPersonalCost;

    @SerializedName("is_nmu_sent")
    private boolean isNmuSent;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public NonMoimingUserVO convertToVO(){

        // 여기서 LocalDateTime 으로 CreatedAt / UpdatedAt 변환 후 VO로 저장.

        NonMoimingUserVO generateVO = new NonMoimingUserVO();

        generateVO.setUuid(this.uuid);
        generateVO.setNmuName(this.nmuName);
        generateVO.setNmuPersonalCost(this.nmuPersonalCost);
        generateVO.setNmuSent(this.isNmuSent);

        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        generateVO.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        if(this.updatedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            generateVO.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }

        return generateVO;

    }

    @Override
    public String toString() {
        return "NonMoimingDTO: {"
                + "\nnmuName= " + nmuName
                + "\nisNmuSent= " + String.valueOf(isNmuSent)
                + "\nnmuPersonalCost= " + nmuPersonalCost
                + "\n}";

    }
}
