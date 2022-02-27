package com.example.moimingrelease.moiming_model.response_dto;

import com.example.moimingrelease.moiming_model.moiming_vo.GroupPaymentVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class GroupPaymentResponseDTO {

    @SerializedName("uuid")
    private UUID uuid;

    @SerializedName("payment_date")
    private String paymentDate;

    @SerializedName("payment_name")
    private String paymentName;

    @SerializedName("payment_cost")
    private int paymentCost;

    @SerializedName("payment_type")
    private boolean paymentType; //true: 수입, false: 지출

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public GroupPaymentVO convertToVO() {

        GroupPaymentVO groupPayment = new GroupPaymentVO();

        groupPayment.setUuid(this.uuid);
        groupPayment.setPaymentName(this.paymentName);
        groupPayment.setPaymentType(this.paymentType);
        groupPayment.setPaymentCost(this.paymentCost);
        groupPayment.setPaymentDate(LocalDate.parse(this.paymentDate));

        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        groupPayment.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        if (this.updatedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            groupPayment.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }


        return groupPayment;
    }
}
