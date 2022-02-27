package com.example.moimingrelease.moiming_model.recycler_view_model;

import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;

import java.util.List;

public class MainRecyclerLinkerData {

    // From GroupVO
//    private MoimingGroupVO moimingGroup;
    private MoimingGroupAndMembersDTO groupData;

    private List<ReceivedNotificationDTO> groupNotification;

/*
    // From UserLinker
    private String recentNotice;
    private int noticeCnt;*/

  /*  public MainRecyclerLinkerData(MoimingGroupAndMembersDTO groupData, String recentNotice, int noticeCnt) {

        this.groupData = groupData;
        this.recentNotice = recentNotice;
        this.noticeCnt = noticeCnt;

    }*/

    public MainRecyclerLinkerData(MoimingGroupAndMembersDTO groupData, List<ReceivedNotificationDTO> groupNotification) {

        this.groupData = groupData;
        this.groupNotification = groupNotification;

    }


    public MoimingGroupAndMembersDTO getGroupData() {
        return groupData;
    }

    public void setGroupData(MoimingGroupAndMembersDTO groupData) {
        this.groupData = groupData;
    }

    public List<ReceivedNotificationDTO> getGroupNotification() {
        return groupNotification;
    }

    public void setGroupNotification(List<ReceivedNotificationDTO> groupNotification) {
        this.groupNotification = groupNotification;
    }
}
