package com.example.moimingrelease.moiming_model.request_dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.UUID;

public class GroupPaymentRequestDTO {

    @SerializedName("group_uuid")
    private UUID groupUuid;

    @SerializedName("payment_date")
    private String paymentDate;

    @SerializedName("payment_name")
    private String paymentName;

    @SerializedName("payment_cost")
    private int paymentCost;

    @SerializedName("payment_type")
    private boolean paymentType; //true: 수입, false: 지출

    public GroupPaymentRequestDTO(UUID groupUuid, String paymentName, int paymentCost, boolean paymentType, String paymentDate) {

        this.groupUuid= groupUuid;
        this.paymentName = paymentName;
        this.paymentCost = paymentCost;
        this.paymentType = paymentType;
        this.paymentDate = paymentDate;

    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public int getPaymentCost() {
        return paymentCost;
    }

    public void setPaymentCost(int paymentCost) {
        this.paymentCost = paymentCost;
    }

    public boolean isPaymentType() {
        return paymentType;
    }

    public void setPaymentType(boolean paymentType) {
        this.paymentType = paymentType;
    }
}
