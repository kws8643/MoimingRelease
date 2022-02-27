package com.example.moimingrelease.moiming_model.recycler_view_model;

public class SessionNmuLinkerData {

    private String userName;
    private Integer userCost;
    private boolean isEdited;
    private boolean isAutoChanged;


    public SessionNmuLinkerData(String userName, Integer userCost) {

        this.userName = userName;
        this.userCost = userCost;
        isEdited = false;
        isAutoChanged = false;


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

    public void setUserCost(Integer userCost) {
        this.userCost = userCost;
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
