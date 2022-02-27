package com.example.moimingrelease.moiming_model.extras;

import com.example.moimingrelease.moiming_model.request_dto.PolicyAgreeRequestDTO;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class UserAndPolicyAgreeDTO {

    @SerializedName("user_uuid")
    private UUID userUuid;

    @SerializedName("user_agree_list")
    private List<PolicyAgreeRequestDTO> userAgreeList;

    public UserAndPolicyAgreeDTO(UUID userUuid, List<PolicyAgreeRequestDTO> userAgreeList) {

        this.userUuid = userUuid;
        this.userAgreeList = userAgreeList;

    }
}
