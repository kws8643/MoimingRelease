package com.example.moimingrelease.moiming_model.moiming_vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class NonMoimingUserVO implements Serializable {

    private UUID uuid;

    private String nmuName;

    private Integer nmuPersonalCost;

    private Boolean isNmuSent;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getNmuName() {
        return nmuName;
    }

    public void setNmuName(String nmuName) {
        this.nmuName = nmuName;
    }

    public Integer getNmuPersonalCost() {
        return nmuPersonalCost;
    }

    public void setNmuPersonalCost(Integer nmuPersonalCost) {
        this.nmuPersonalCost = nmuPersonalCost;
    }

    public boolean isNmuSent() {
        return isNmuSent;
    }

    public void setNmuSent(boolean nmuSent) {
        isNmuSent = nmuSent;
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


}
