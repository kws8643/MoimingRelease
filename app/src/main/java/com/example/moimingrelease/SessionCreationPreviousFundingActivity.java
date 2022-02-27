package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.moimingrelease.frag_session_creation_previous_funding.ChoosePrevFundingFragment;
import com.example.moimingrelease.frag_session_creation_previous_funding.ViewFundingInfoFragment;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

import java.io.Serializable;
import java.util.List;

public class SessionCreationPreviousFundingActivity extends AppCompatActivity {

    public final String PREV_FUNDING_LOG = "GROUP_PREV_FUNDING";

    private MoimingGroupVO curGroup;
    private MoimingUserVO curUser;

    private FragmentManager manager;
    ChoosePrevFundingFragment fragChooseFunding;
    ViewFundingInfoFragment fragFundingInfo;

    private void receiveIntent(){

        Intent receivedIntent = getIntent();

        if(receivedIntent.getExtras() != null){

            curGroup = receivedIntent.getParcelableExtra(getResources().getString(R.string.moiming_group_data_key));
            curUser = (MoimingUserVO) receivedIntent.getSerializableExtra(getResources().getString(R.string.moiming_user_data_key));

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_creation_previous_funding);

        receiveIntent();

        initView();

        initParams();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frame_prev_funding, fragChooseFunding).commit();

    }

    private void initView(){

    }

    private void initParams(){

        manager = getSupportFragmentManager();

        fragChooseFunding = new ChoosePrevFundingFragment();
        fragFundingInfo = new ViewFundingInfoFragment();

    }

    public void changeFragments(int index, MoimingSessionVO sessionVO) {

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);

        if (index == 0) { // Choose 로 돌아옴

            transaction.replace(R.id.frame_prev_funding, fragChooseFunding).commit(); // 계속 replace 를 해주는 것

        } else { // ViewGroupInfo 로 이동

            Bundle dataBundle = new Bundle();

            dataBundle.putSerializable(getResources().getString(R.string.moiming_session_data_key), sessionVO);
            fragFundingInfo.setArguments(dataBundle);

            transaction.replace(R.id.frame_prev_funding, fragFundingInfo).commit();

        }
    }


    public MoimingGroupVO getCurGroup(){

        return this.curGroup;
    }

    public MoimingUserVO getCurUser(){

        return this.curUser;

    }
}