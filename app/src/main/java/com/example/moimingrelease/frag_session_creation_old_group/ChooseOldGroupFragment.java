package com.example.moimingrelease.frag_session_creation_old_group;

import android.os.Bundle;
import android.se.omapi.Session;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionCreationInOldGroupActivity;
import com.example.moimingrelease.app_adapter.SessionCreationOldGroupsAdapter;
import com.example.moimingrelease.app_listener_interface.SessionCreationViewOldGroupListener;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.SearchedGroupViewData;

import java.util.ArrayList;
import java.util.List;

public class ChooseOldGroupFragment extends Fragment {

    private SessionCreationInOldGroupActivity activity;

    private RecyclerView recyclerOldGroups;
    private SessionCreationOldGroupsAdapter oldGroupsAdapter;

    private ArrayList<MoimingGroupAndMembersDTO> curUserGroupAdapterList;

    private EditText searchGroup;
    private ImageView btnFinish;

    private TextWatcher onSearchGroup = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = searchGroup.getText().toString();

            searchGroupAndMembers(text);

        }
    };

    private void searchGroupAndMembers(String text) {

        curUserGroupAdapterList.clear();

        if (text.length() == 0) { // ???????????? ???????????? ????????? ??????

            curUserGroupAdapterList.addAll(activity.getGroupAndMembersRawList());

        } else {

            // 3. ???????????? ?????? ?????? ??? groupName ??? ????????? ????????????.
            searchInGroupName(text);

            // 4. ??? ?????? ???????????? ???????????? ?????? ????????? ????????? ????????????. ?????? ?????? isMember??? true??? ??????.
            searchInGroupMembersName(text);

        }

        oldGroupsAdapter.notifyDataSetChanged();

    }

    // ArrayList<MoimingGroupAndMembersDTO> userGroupAndMemebersList;
    private void searchInGroupName(String text) {

        for (int i = 0; i < activity.getGroupAndMembersRawList().size(); i++) {

            MoimingGroupAndMembersDTO userGroupData = activity.getGroupAndMembersRawList().get(i);

            if (userGroupData.getMoimingGroup().getGroupName().toLowerCase().contains(text)) { // ?????? ????????? ?????? ?????????.

                curUserGroupAdapterList.add(userGroupData);

            }
        }
    }

    private void searchInGroupMembersName(String text) {

        for (int i = 0; i < activity.getGroupAndMembersRawList().size(); i++) {

            MoimingGroupAndMembersDTO userGroupData = activity.getGroupAndMembersRawList().get(i);
            List<MoimingMembersDTO> membersList = userGroupData.getMoimingMembersList();

            for (int j = 0; j < membersList.size(); j++) {

                if (membersList.get(j).getUserName().toLowerCase().contains(text)) { // ???????????? ?????? ?????? ?????????.

                    if (!curUserGroupAdapterList.contains(userGroupData)) {
                        curUserGroupAdapterList.add(userGroupData);
                    }

                }
            }


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_session_creation_old_choose_group, container, false);

        initView(view);

        initParams();

        initRecyclerView();

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        return view;

    }

    private void initView(View view) {

        recyclerOldGroups = view.findViewById(R.id.choose_old_group_recycler_view);

        searchGroup = view.findViewById(R.id.input_old_group_search);
        searchGroup.addTextChangedListener(onSearchGroup);

        btnFinish = view.findViewById(R.id.btn_back_choose_group);
    }

    private void initParams() {

        activity = (SessionCreationInOldGroupActivity) getActivity();

        curUserGroupAdapterList = new ArrayList<>();
        curUserGroupAdapterList.addAll(activity.getGroupAndMembersRawList());

    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerOldGroups.setLayoutManager(linearLayoutManager);

        oldGroupsAdapter = new SessionCreationOldGroupsAdapter(activity.getApplicationContext(), curUserGroupAdapterList, chooseGroupListener);
        recyclerOldGroups.setAdapter(oldGroupsAdapter);

    }


    public SessionCreationViewOldGroupListener chooseGroupListener = new SessionCreationViewOldGroupListener() {
        @Override
        public void moveToViewGroup(MoimingGroupVO groupData, List<MoimingMembersDTO> groupMembersList) {

            // ?????? ?????? ???????????? ????????? ??????????????? ????????? ????????????
            activity.changeFragments(1, groupData, groupMembersList);

        }
    };

}

