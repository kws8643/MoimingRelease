package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.moimingrelease.app_adapter.MainRecyclerAdapter;
import com.example.moimingrelease.app_adapter.RecyclerViewItemDecoration;
import com.example.moimingrelease.app_adapter.SessionCreationOldGroupsAdapter;
import com.example.moimingrelease.app_listener_interface.SessionCreationViewOldGroupListener;
import com.example.moimingrelease.frag_session_creation_old_group.ChooseOldGroupFragment;
import com.example.moimingrelease.frag_session_creation_old_group.ViewGroupInfoFragment;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SessionCreationInOldGroupActivity extends AppCompatActivity {


    // Main Activity 로부터 MoimingGroupVO 를 받아온다.
    // 해당 정보들을 통해서 List<GroupMemberDTO> 를 형성한다.
    //
    //

    private MoimingUserVO curUser;
    private ArrayList<MoimingGroupAndMembersDTO> groupAndMembersRawList;

    private FragmentManager manager;
    ChooseOldGroupFragment fragChooseOldGroup;
    ViewGroupInfoFragment fragViewGroupInfo;

    public ArrayList<MoimingGroupAndMembersDTO> getGroupAndMembersRawList(){

        return this.groupAndMembersRawList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_creation_in_old_group);

        receiveIntent();

        initView();

        initParams();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frame_session_creation_old_group, fragChooseOldGroup).commit();
    }


    private void receiveIntent() {

        Intent receivedData = getIntent();

        if (receivedData.getExtras() != null) {

            curUser = (MoimingUserVO) receivedData.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            groupAndMembersRawList = receivedData.getParcelableArrayListExtra(getResources().getString(R.string.moiming_group_and_members_data_key));
        }
    }

    private void initView() {


    }

    private void initParams() {

        manager = getSupportFragmentManager();
        fragChooseOldGroup = new ChooseOldGroupFragment();
        fragViewGroupInfo = new ViewGroupInfoFragment();

    }

    public void changeFragments(int index, MoimingGroupVO selectedGroupData, List<MoimingMembersDTO> groupMembersList) {

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);

        if (index == 0) { // Choose 로 돌아옴

            transaction.replace(R.id.frame_session_creation_old_group, fragChooseOldGroup).commit();

        } else { // ViewGroupInfo 로 이동 // MoimingGroupVO 전달

            Bundle dataBundle = new Bundle();

            dataBundle.putSerializable(getResources().getString(R.string.moiming_user_data_key), curUser);
            dataBundle.putSerializable(getResources().getString(R.string.moiming_group_data_key), selectedGroupData);
            dataBundle.putSerializable(getResources().getString(R.string.group_members_data_key), (Serializable) groupMembersList);
            fragViewGroupInfo.setArguments(dataBundle);

            transaction.replace(R.id.frame_session_creation_old_group, fragViewGroupInfo).commit();

        }

    }


}