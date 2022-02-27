package com.example.moimingrelease.app_listener_interface;

import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;

import java.util.List;

public interface ViewPrevSessionListener {

    void moveToViewSession(MoimingSessionVO sessionVO);

}
