package com.example.moimingrelease.moiming_model.recycler_view_model;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;

public class SessionStatusMemberLinkerData {

    private MoimingUserVO selectedUser;
    private NonMoimingUserVO nmuUser;
    private Integer personalCost;
    private boolean isSent;

    // 멤버들
    public SessionStatusMemberLinkerData(MoimingUserVO selectedUser, Integer personalCost, boolean isSent) {

        this.selectedUser = selectedUser;
        this.personalCost = personalCost;
        this.isSent = isSent;

    }

    // NMU들
    public SessionStatusMemberLinkerData(NonMoimingUserVO nmuUser) {

        this.nmuUser = nmuUser;
    }

    public MoimingUserVO getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(MoimingUserVO selectedUser) {
        this.selectedUser = selectedUser;
    }

    public Integer getPersonalCost() {
        return personalCost;
    }

    public void setPersonalCost(Integer personalCost) {
        this.personalCost = personalCost;
    }

    public boolean isSent() {
        return isSent;
    }

    public NonMoimingUserVO getNmuUser() {
        return nmuUser;
    }

    public void setNmuUser(NonMoimingUserVO nmuUser) {
        this.nmuUser = nmuUser;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }
}
