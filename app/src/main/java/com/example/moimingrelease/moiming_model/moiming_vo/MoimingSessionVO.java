package com.example.moimingrelease.moiming_model.moiming_vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.annotations.Nullable;

public class MoimingSessionVO implements Serializable {

    private UUID uuid;

    @SerializedName("session_type")
    private Integer sessionType; //0: 모금 1: 더치페이

    @SerializedName("session_name")
    private String sessionName;

    @SerializedName("session_creator_uuid")
    private UUID sessionCreatorUuid;

    @SerializedName("session_member_cnt")
    private Integer sessionMemberCnt;

    @SerializedName("cur_sender_cnt")
    private Integer curSenderCnt;

    @SerializedName("total_cost")
    private Integer totalCost;

    @SerializedName("single_cost")
    private Integer singleCost; // 개인별 금액 조정 상관 없이 원래 내야하는 정산 금액 // n빵 된 금액

    @SerializedName("cur_cost")
    private Integer curCost;

    @SerializedName("is_finished")
    private Boolean isFinished;

    @SerializedName("deleted_at")
    private LocalDateTime deletedAt;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;

    private List<NonMoimingUserVO>  nmuList;

    private List<UserSessionLinkerVO> usLinkerList;

    public List<NonMoimingUserVO> getNmuList() {
        return nmuList;
    }

    public void setNmuList(List<NonMoimingUserVO> nmuList) {
        this.nmuList = nmuList;
    }

    public List<UserSessionLinkerVO> getUsLinkerList() {
        return usLinkerList;
    }

    public void setUsLinkerList(List<UserSessionLinkerVO> usLinkerList) {
        this.usLinkerList = usLinkerList;
    }

    public Integer getSingleCost() {
        return singleCost;
    }

    public void setSingleCost(Integer singleCost) {
        this.singleCost = singleCost;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Integer getSessionType() {
        return sessionType;
    }

    public void setSessionType(Integer sessionType) {
        this.sessionType = sessionType;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public UUID getSessionCreatorUuid() {
        return sessionCreatorUuid;
    }

    public void setSessionCreatorUuid(UUID sessionCreatorUuid) {
        this.sessionCreatorUuid = sessionCreatorUuid;
    }

    public Integer getSessionMemberCnt() {
        return sessionMemberCnt;
    }

    public void setSessionMemberCnt(Integer sessionMemberCnt) {
        this.sessionMemberCnt = sessionMemberCnt;
    }

    public Integer getCurSenderCnt() {
        return curSenderCnt;
    }

    public void setCurSenderCnt(Integer curSenderCnt) {
        this.curSenderCnt = curSenderCnt;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public Integer getCurCost() {
        return curCost;
    }

    public void setCurCost(Integer curCost) {
        this.curCost = curCost;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {

        return "MoimingSessionVO: {\nuuid = " + uuid.toString()
                + "\nsessionType= " + sessionType
                + "\nsessionName= " + sessionName
                + "\nsessionMemberCnt= " + sessionMemberCnt
                + "\ncurSenderCnt= " + curSenderCnt
                + "\ntotalCost= " + totalCost
                + "\nsingleCost=" + singleCost
                + "\ncurCost= " + curCost
                + "\nisFinished= " + isFinished
                + "\ndeletedAt= " + deletedAt
                + "\ncreatedAt = " + createdAt
                + "\nupdatedAt = " + updatedAt + "\n}";


    }

}
