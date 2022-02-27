package com.example.moimingrelease.moiming_model.request_dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

public class MoimingSessionRequestDTO {

    @SerializedName("session_type")
    private Integer sessionType;

    @SerializedName("session_name")
    private String sessionName;

    @SerializedName("moiming_group_uuid")
    private UUID moimingGroupUuid;

    @SerializedName("session_creator_uuid")
    private UUID sessionCreatorUuid;

    @SerializedName("session_creator_cost")
    private Integer sessionCreatorCost;

    @SerializedName("session_member_cnt")
    private Integer sessionMemberCnt;

    @SerializedName("total_cost")
    private Integer totalCost;

    @SerializedName("single_cost")
    private Integer singleCost;

    public MoimingSessionRequestDTO(UUID sessionCreatorUuid, UUID moimingGroupUuid, String sessionName, Integer sessionCreatorCost, Integer sessionType, Integer sessionMemberCnt, Integer totalCost, Integer singleCost) {

        this.sessionCreatorUuid = sessionCreatorUuid;
        this.moimingGroupUuid = moimingGroupUuid;
        this.sessionName = sessionName;
        this.sessionCreatorCost = sessionCreatorCost;
        this.sessionType = sessionType;
        this.sessionMemberCnt = sessionMemberCnt;
        this.totalCost = totalCost;
        this.singleCost = singleCost;

    }


    @Override
    public String toString() {

        return "MoimingSessionRequestDTO: {\nsessionCreatorUuid = " + sessionCreatorUuid.toString()
                + "\nmoimingGroupUuid = " + moimingGroupUuid.toString()
                + "\nsessionName = " + sessionName
                + "\nsessionType= " + sessionType
                + "\nsessionMemberCnt= " + sessionMemberCnt
                + "\ntotalCost= " + totalCost + "}";

    }

    public UUID getMoimingGroupUuid() {
        return moimingGroupUuid;
    }

    public void setMoimingGroupUuid(UUID moimingGroupUuid) {
        this.moimingGroupUuid = moimingGroupUuid;
    }
}
