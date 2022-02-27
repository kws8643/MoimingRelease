package com.example.moimingrelease.moiming_model.moiming_vo;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserSessionLinkerVO implements Serializable {

    private Integer personalCost;

    private boolean isSent;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private MoimingUserVO moimingUser;

    public Integer getPersonalCost() {
        return personalCost;
    }

    public void setPersonalCost(Integer personalCost) {
        this.personalCost = personalCost;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
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

    public MoimingUserVO getMoimingUser() {
        return moimingUser;
    }

    public void setMoimingUser(MoimingUserVO moimingUser) {
        this.moimingUser = moimingUser;
    }
}
