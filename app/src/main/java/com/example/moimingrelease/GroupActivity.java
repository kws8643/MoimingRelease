package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

public class GroupActivity extends AppCompatActivity {

    public static final String MOIMING_GROUP_DATA_KEY = "current_moiming_group";


    MoimingUserVO curUser;
    MoimingGroupVO curGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        initView();
        initParams();

        Toast.makeText(getApplicationContext(), curGroup.getGroupName() + "_ 그룹에 오신걸 환영합니다, " + curUser.getUserName() + "_님!", Toast.LENGTH_SHORT).show();
    }

    private void initView() {


    }


    private void initParams() {

        receiveIntent(); // 경우에 따라 다르게 받는 Intent data!


    }

    private void receiveIntent() {

        Intent dataIntent = getIntent();

        // 1. GROUP CRAETION 으로 올 경우.
        curUser = (MoimingUserVO) dataIntent.getExtras().getSerializable(MainActivity.MOIMING_USER_DATA_KEY);
        curGroup = (MoimingGroupVO) dataIntent.getExtras().getSerializable(MOIMING_GROUP_DATA_KEY);


    }
}