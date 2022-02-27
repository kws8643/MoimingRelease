package com.example.moimingrelease.moiming_model.extras;

import com.example.moimingrelease.moiming_model.response_dto.NonMoimingUserResponseDTO;
import com.example.moimingrelease.moiming_model.response_dto.USLinkerResponseDTO;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SessionMembersDTO {

    @SerializedName("session_creator_info")
    private MoimingMembersDTO sessionCreatorInfo;

    @SerializedName("session_moiming_member_list")
    private List<USLinkerResponseDTO> sessionMoimingMemberList;

    @SerializedName("session_nmu_list")
    private List<NonMoimingUserResponseDTO> sessionNmuList;

    public List<USLinkerResponseDTO> getSessionMoimingMemberList() {
        return sessionMoimingMemberList;
    }

    public MoimingMembersDTO getSessionCreatorInfo() {
        return sessionCreatorInfo;
    }

    public void setSessionCreatorInfo(MoimingMembersDTO sessionCreatorInfo) {
        this.sessionCreatorInfo = sessionCreatorInfo;
    }

    public void setSessionMoimingMemberList(List<USLinkerResponseDTO> sessionMoimingMemberList) {
        this.sessionMoimingMemberList = sessionMoimingMemberList;
    }

    public List<NonMoimingUserResponseDTO> getSessionNmuList() {
        return sessionNmuList;
    }

    public void setSessionNmuList(List<NonMoimingUserResponseDTO> sessionNmuList) {
        this.sessionNmuList = sessionNmuList;
    }
}
