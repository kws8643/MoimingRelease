package com.example.moimingrelease.moiming_model.request_dto;


import com.google.gson.annotations.SerializedName;

public class PolicyAgreeRequestDTO {

    @SerializedName("policy_number")
    private int policyNumber;

    @SerializedName("policy_info")
    private String policyInfo;

    @SerializedName("is_agreed")
    private boolean isAgreed;

    public PolicyAgreeRequestDTO(int policyNumber, String policyInfo, boolean isAgreed) {

        this.isAgreed = isAgreed;
        this.policyInfo = policyInfo;
        this.policyNumber = policyNumber;

    }

}
