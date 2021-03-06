package com.example.moimingrelease.moiming_model.moiming_vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserGroupLinkerVO implements Serializable {

    private Long id;

  /*  @SerializedName("recent_notice")
    private String recentNotice;

    @SerializedName("notice_cnt")
    private Integer noticeCnt;*/

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;

    @SerializedName("moiming_user")
    private MoimingUserVO moimingUser;

    @SerializedName("moiming_group")
    private MoimingGroupVO moimingGroup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
/*

    public String getRecentNotice() {
        return recentNotice;
    }

    public void setRecentNotice(String recentNotice) {
        this.recentNotice = recentNotice;
    }

    public Integer getNoticeCnt() {
        return noticeCnt;
    }

    public void setNoticeCnt(Integer noticeCnt) {
        this.noticeCnt = noticeCnt;
    }
*/

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


    public MoimingUserVO getMoimingUser() {
        return moimingUser;
    }

    public void setMoimingUser(MoimingUserVO moimingUser) {
        this.moimingUser = moimingUser;
    }

    public MoimingGroupVO getMoimingGroup() {
        return moimingGroup;
    }

    public void setMoimingGroup(MoimingGroupVO moimingGroup) {
        this.moimingGroup = moimingGroup;
    }
}
