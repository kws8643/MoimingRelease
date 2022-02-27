package com.example.moimingrelease.moiming_model.recycler_view_model;

import java.util.UUID;

public class SessionMemberLinkerData { // For Recy. View.

    private UUID uuid; // 유저 uuid

    private String userPfImg;

    private String userName;

    private Integer userCost;

    private boolean isEdited; // 교체된 애인지

    private boolean isAutoChanged;


    public SessionMemberLinkerData(UUID uuid, String userName, String userPfImg, Integer userCost) {

        this.uuid = uuid;
        this.userName = userName;
        this.userPfImg = userPfImg;
        this.userCost = userCost;
        isEdited = false;
        isAutoChanged = false;

    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserCost() {
        return userCost;
    }

    public void setUserCost(Integer userCharge) {
        this.userCost = userCharge;
    }

    public String getUserPfImg() {
        return userPfImg;
    }

    public void setUserPfImg(String userPfImg) {
        this.userPfImg = userPfImg;
    }

    public boolean getIsEdited() {
        return this.isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public boolean isAutoChanged() {
        return isAutoChanged;
    }

    public void setAutoChanged(boolean autoChanged) {
        isAutoChanged = autoChanged;
    }
}
