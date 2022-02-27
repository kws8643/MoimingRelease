package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {


    private MoimingUserVO curUser;
    private List<MoimingGroupAndMembersDTO> groupAndMemberList;
    private List<ReceivedNotificationDTO> notificationList;

    private void receiveIntent(){

        Intent receivedData = getIntent();

        if(receivedData!= null){

            curUser = (MoimingUserVO) receivedData.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            groupAndMemberList = receivedData.getParcelableArrayListExtra(getResources().getString(R.string.search_group_data_key));
            notificationList = receivedData.getParcelableArrayListExtra(getResources().getString(R.string.moiming_notification_data_key));

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        receiveIntent();

        initView();

        initParams();

    }

    private void initView(){


    }

    private void initParams(){


    }
}