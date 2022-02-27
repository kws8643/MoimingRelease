package com.example.moimingrelease.moiming_model.recycler_view_model;

import java.util.UUID;

public class PreviousSessionMemberStatusData {

//    private UUID userUuid;

    private String userName;

    private Integer userCost;

    public PreviousSessionMemberStatusData(String userName, Integer userCost){

        this.userName = userName;
        this.userCost  = userCost;
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
}
