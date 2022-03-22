package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.moimingrelease.app_adapter.NotificationRecyclerAdapter;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    //전달은 필요
    private MoimingUserVO curUser; // For transferring to activity
    private List<MoimingGroupAndMembersDTO> groupAndMemberList;
    private List<ReceivedNotificationDTO> notificationList; // 얘만 가지고 파싱 가능


    private NestedScrollView notiScroll;
    private RecyclerView notificationRecycler;

    private NotificationRecyclerAdapter notiAdapter;

    private void receiveIntent() {

        Intent receivedData = getIntent();

        if (receivedData != null) {

            curUser = (MoimingUserVO) receivedData.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            groupAndMemberList = receivedData.getParcelableArrayListExtra(getResources().getString(R.string.moiming_group_and_members_data_key));
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

        initRecyclerView();

    }

    private void initView() {

        notiScroll = findViewById(R.id.noti_scroll);
        notiScroll.setNestedScrollingEnabled(true);

        notificationRecycler = findViewById(R.id.notification_recycler);

    }

    private void initParams() {


    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        notificationRecycler.setLayoutManager(linearLayoutManager);

        sortNotificationList();

        notiAdapter = new NotificationRecyclerAdapter(this, curUser, groupAndMemberList, notificationList);

        notificationRecycler.setAdapter(notiAdapter);

    }


    private void sortNotificationList() {

        List<ReceivedNotificationDTO> tempList = new ArrayList<>();
        tempList.addAll(notificationList);
        notificationList.clear();

        // 1. system Comes First
        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).getNotification().getSentActivity().equals("system")) {
                notificationList.add(tempList.get(i));
                tempList.remove(i);
                i--; // 제거 시 하나가 밀림
            }
        }

        Collections.sort(notificationList, byDate);

        // 2. Else goes as time
        Collections.sort(tempList, byDate);
        notificationList.addAll(tempList);

    }


    Comparator<ReceivedNotificationDTO> byDate = new Comparator<ReceivedNotificationDTO>() {
        @Override
        public int compare(ReceivedNotificationDTO dto1, ReceivedNotificationDTO dto2) {
            return dto2.getNotification().getCreatedAtForm().compareTo(dto1.getNotification().getCreatedAtForm());
        }
    };
}