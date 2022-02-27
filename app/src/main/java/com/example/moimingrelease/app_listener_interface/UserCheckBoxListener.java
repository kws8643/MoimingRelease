package com.example.moimingrelease.app_listener_interface;

import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;

public interface UserCheckBoxListener {

    void onCheckBoxClick(boolean isAdded, MoimingMembersDTO member);
    
}
