package com.example.moimingrelease.moiming_model.request_dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class GroupAndSessionCreationDTO {

    // 초대하는 그룹원들의 uuid
    @SerializedName("members_uuid_list")
    private List<UUID> membersUuidList;

    @SerializedName("us_data_list")
    private List<USLinkerRequestDTO> usDataList;

    @SerializedName("group_request")
    private MoimingGroupRequestDTO groupRequest;

    // 정산활동 RequestDTO
    @SerializedName("session_request")
    private MoimingSessionRequestDTO sessionRequest;


    public GroupAndSessionCreationDTO(MoimingGroupRequestDTO groupRequest, MoimingSessionRequestDTO sessionRequest, List<UUID> membersUuidList, List<USLinkerRequestDTO> usDataList) {

        this.groupRequest = groupRequest;
        this.sessionRequest = sessionRequest;
        this.membersUuidList = membersUuidList;
        this.usDataList = usDataList;

    }

}
