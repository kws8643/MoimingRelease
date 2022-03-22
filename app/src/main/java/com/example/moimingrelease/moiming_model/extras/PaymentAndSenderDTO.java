package com.example.moimingrelease.moiming_model.extras;

import com.example.moimingrelease.moiming_model.request_dto.GroupPaymentRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.GroupPaymentResponseDTO;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class PaymentAndSenderDTO {

    @SerializedName("sent_user_uuid")
    private UUID sentUserUuid;

    @SerializedName("payment_data")
    private GroupPaymentRequestDTO paymentData;

    public PaymentAndSenderDTO(UUID sentUserUuid, GroupPaymentRequestDTO paymentData){

        this.sentUserUuid = sentUserUuid;
        this.paymentData = paymentData;

    }

    public UUID getSentUserUuid() {
        return sentUserUuid;
    }

    public void setSentUserUuid(UUID sentUserUuid) {
        this.sentUserUuid = sentUserUuid;
    }

    public GroupPaymentRequestDTO getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(GroupPaymentRequestDTO paymentData) {
        this.paymentData = paymentData;
    }
}
