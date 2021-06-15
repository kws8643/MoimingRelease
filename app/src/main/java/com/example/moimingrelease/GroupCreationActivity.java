package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.moimingrelease.fragments_group_creation.GroupCreationInfoFragment;
import com.example.moimingrelease.fragments_group_creation.GroupCreationMembersFragment;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

public class GroupCreationActivity extends AppCompatActivity {

    public static final int FRAGMENT_INFO_INDEX = 0;
    public static final int FRAGMENT_MEMBERS_INDEX = 1;
    public static final String GROUP_CREATION_TAG = "GROUP_CREATION_TAG";

    public MoimingUserVO curMoimingUser;

    FragmentManager fragmentManager;
    private GroupCreationInfoFragment infoFragment;
    private GroupCreationMembersFragment membersFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);

        initView();

        initParams();

        fragmentManager.beginTransaction().add(R.id.frame_group_creation, infoFragment).commit();


    }

    private void initView(){


    }


    private void initParams(){

        Intent userInfo = getIntent();
        curMoimingUser = (MoimingUserVO) userInfo.getExtras().getSerializable(MainActivity.MOIMING_USER_DATA_KEY);

        Log.w(GroupCreationActivity.GROUP_CREATION_TAG,curMoimingUser.toString());

        fragmentManager = getSupportFragmentManager();
        infoFragment = new GroupCreationInfoFragment();
        membersFragment= new GroupCreationMembersFragment();

    }

    public void changeFragment(int index, Bundle fragData){

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);

        if(index == FRAGMENT_INFO_INDEX){

            transaction.replace(R.id.frame_group_creation, infoFragment).commit();

        }else if(index == FRAGMENT_MEMBERS_INDEX){

            membersFragment.setArguments(fragData);
            transaction.replace(R.id.frame_group_creation, membersFragment).commit();

        }

    }


}