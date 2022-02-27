package com.example.moimingrelease.moiming_model.recycler_view_model;

import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;

import java.util.List;

public class SearchedGroupViewData {

//    private MoimingGroupVO moimingGroup;

    private MoimingGroupAndMembersDTO moimingGroupAndMembers;
    private boolean isMemberSearched;

    private List<String> searchedGroupUsers;


    public SearchedGroupViewData(MoimingGroupAndMembersDTO moimingGroupAndMembers, boolean isMemberSearched, List<String> searchedGroupUsers) {

        this.moimingGroupAndMembers = moimingGroupAndMembers;
        this.isMemberSearched = isMemberSearched;
        this.searchedGroupUsers = searchedGroupUsers;

    }

    /*public MoimingGroupVO getMoimingGroupVO() {
        return moimingGroup;
    }

    public void setMoimingGroupVO(MoimingGroupVO moimingGroup) {
        this.moimingGroup = moimingGroup;
    }*/

    public MoimingGroupAndMembersDTO getMoimingGroupAndMembers() {
        return this.moimingGroupAndMembers;
    }

    public void setMoimingGroupAndMembers(MoimingGroupAndMembersDTO moimingGroupAndMembers) {
        this.moimingGroupAndMembers = moimingGroupAndMembers;
    }

    public List<String> getSearchedGroupUsers() {
        return this.searchedGroupUsers;
    }

    public void setSearchedGroupUsers(List<String> searchedGroupUsers) {
        this.searchedGroupUsers = searchedGroupUsers;
    }

    public boolean isMemberSearched() {
        return isMemberSearched;
    }

    public void setMemberSearched(boolean memberSearched) {
        isMemberSearched = memberSearched;
    }
}
