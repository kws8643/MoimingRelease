package com.example.moimingrelease.moiming_model.extras;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingSessionResponseDTO;
import com.google.gson.annotations.SerializedName;

public class SessionAndUserStatusDTO {

    @SerializedName("moiming_session")
    private MoimingSessionResponseDTO moimingSessionResponseDTO;

    @SerializedName("creator_name")
    private String creatorName;

    @SerializedName("is_session_finished")
    private boolean isSessionFinished;


    /**
     0 = 내 정산 1 = 송금 필요 2 = 송금 완료 3 = 송금 확인 중 4 = 미참여
     */
    @SerializedName("cur_user_status")
    private Integer curUserStatus; //0,1,2,3 으로 나뉜다

    @SerializedName("cur_user_cost")
    private Integer curUserCost;

    public MoimingSessionResponseDTO getMoimingSessionResponseDTO() {
        return moimingSessionResponseDTO;
    }

    public void setMoimingSessionResponseDTO(MoimingSessionResponseDTO moimingSessionResponseDTO) {
        this.moimingSessionResponseDTO = moimingSessionResponseDTO;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public boolean isSessionFinished() {
        return isSessionFinished;
    }

    public void setSessionFinished(boolean sessionFinished) {
        isSessionFinished = sessionFinished;
    }

    public Integer getCurUserStatus() {
        return curUserStatus;
    }

    public void setCurUserStatus(Integer curUserStatus) {
        this.curUserStatus = curUserStatus;
    }

    public Integer getCurUserCost() {
        return curUserCost;
    }

    public void setCurUserCost(Integer curUserCost) {
        this.curUserCost = curUserCost;
    }
}
