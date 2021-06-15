package com.example.moimingrelease.moiming_model.request_dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UGLinkerCreationRequestDTO {

    // 있어야 하는 Data
    // 1. Linking 대상 Group
    // 2. Linking 대상 UserList
    @SerializedName("group_uuid")
    private UUID groupUuid;

    @SerializedName("members_uuid")
    private Map<Integer, UUID> membersUuid;


    public UGLinkerCreationRequestDTO(UUID groupUuid, Map<Integer, UUID> membersUuid) {

        this.groupUuid = groupUuid;
        this.membersUuid = membersUuid;

    }

}
