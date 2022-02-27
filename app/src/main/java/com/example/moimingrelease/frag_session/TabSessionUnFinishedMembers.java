package com.example.moimingrelease.frag_session;

import android.os.Bundle;
import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionActivity;
import com.example.moimingrelease.app_adapter.RecyclerViewItemDecoration;
import com.example.moimingrelease.app_adapter.SessionNormalMemberAdapter;
import com.example.moimingrelease.app_adapter.SessionUnfinishedMembersAdapter;
import com.example.moimingrelease.app_adapter.SessionUnfinishedNmusAdapter;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionStatusMemberLinkerData;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.UserSessionLinkerVO;

import java.util.ArrayList;
import java.util.List;

public class TabSessionUnFinishedMembers extends Fragment {

    private TextView textMember1, textNmu1;
    private TextView btnSendAllMembers;
    private TextView btnSendKakao;

    private SessionActivity sessionActivity;
    private RecyclerView memberRecycler, nmuRecycler;
    private SessionUnfinishedMembersAdapter membersAdapter;
    private SessionUnfinishedNmusAdapter nmusAdapter;

    private SessionNormalMemberAdapter normalMemberViewAdapter;
    private SessionNormalMemberAdapter normalMemberNmuViewAdapter;

    private List<SessionStatusMemberLinkerData> unfinishedMemberDataList;
    private List<SessionStatusMemberLinkerData> unfinishedNmuDataList;


    public void requestAdapterConfirmCheckedUser(){

        membersAdapter.checkConfirmingUser(sessionActivity.stateChangedUserUuid);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_session_unfinished_members, container, false);

        initView(view);

        initParams();

        initRecyclerView();

        btnSendAllMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionActivity.sendFinishFCMRequest(true, null, 0);
            }
        });

        return view;
    }

    private void initView(View view) {

        memberRecycler = view.findViewById(R.id.session_moiming_member_status);
        nmuRecycler = view.findViewById(R.id.session_nmu_member_status);

        textMember1 = view.findViewById(R.id.text_session_member_1);
        textNmu1 = view.findViewById(R.id.text_session_nmu_1);

        btnSendAllMembers = view.findViewById(R.id.btn_send_all_members);
        btnSendKakao = view.findViewById(R.id.btn_send_kakao_request);

    }

    private void initParams() {

        sessionActivity = (SessionActivity) getActivity();

        unfinishedMemberDataList = new ArrayList<>();
        unfinishedNmuDataList = new ArrayList<>();

    }


    public void initRecyclerView() {

        buildLists();

        if(!sessionActivity.isSessionRefreshing) {
            LinearLayoutManager membersLayoutManager = new LinearLayoutManager(sessionActivity);
            memberRecycler.setLayoutManager(membersLayoutManager);


            LinearLayoutManager nmuLayoutManager = new LinearLayoutManager(sessionActivity);
            nmuRecycler.setLayoutManager(nmuLayoutManager);

            if (sessionActivity.curUserStatus != 0) { // 일반 멤버일경우 // TODO: 내 상태 전달 필요

                textMember1.setVisibility(View.GONE);
                textNmu1.setVisibility(View.GONE);
                btnSendAllMembers.setVisibility(View.GONE);
                btnSendKakao.setVisibility(View.GONE);

                normalMemberViewAdapter = new SessionNormalMemberAdapter(sessionActivity.getApplicationContext()
                        , unfinishedMemberDataList, true, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession());
                memberRecycler.setAdapter(normalMemberViewAdapter);

                normalMemberNmuViewAdapter = new SessionNormalMemberAdapter(sessionActivity.getApplicationContext()
                        , unfinishedNmuDataList, false, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession());
                nmuRecycler.setAdapter(normalMemberNmuViewAdapter);


            } else { // 내가 총무일 경우

                textMember1.setVisibility(View.VISIBLE);
                textNmu1.setVisibility(View.VISIBLE);
                btnSendAllMembers.setVisibility(View.VISIBLE);
                btnSendKakao.setVisibility(View.VISIBLE);

                membersAdapter = new SessionUnfinishedMembersAdapter(sessionActivity.getApplicationContext()
                        , unfinishedMemberDataList, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession()
                        , sessionActivity.checkBoxCallback);
                memberRecycler.setAdapter(membersAdapter);

                nmusAdapter = new SessionUnfinishedNmusAdapter(sessionActivity.getApplicationContext(), unfinishedNmuDataList, sessionActivity.nmuCallback);
                nmuRecycler.setAdapter(nmusAdapter);
            }

            memberRecycler.addItemDecoration(new RecyclerViewItemDecoration(sessionActivity, 6));
            nmuRecycler.addItemDecoration(new RecyclerViewItemDecoration(sessionActivity, 6));
        }else{

            membersAdapter.notifyDataSetChanged();
            nmusAdapter.notifyDataSetChanged();

        }
    }


    private void buildLists() {

        unfinishedNmuDataList.clear();
        unfinishedMemberDataList.clear();

        for (int i = 0; i < sessionActivity.unfinishedMemberLinkerList.size(); i++) {

            UserSessionLinkerVO baseData = sessionActivity.unfinishedMemberLinkerList.get(i);

            SessionStatusMemberLinkerData memberData = new SessionStatusMemberLinkerData(baseData.getMoimingUser()
                    , baseData.getPersonalCost(), baseData.isSent());

            unfinishedMemberDataList.add(memberData);

        }


        for (int i = 0; i < sessionActivity.unfinishedNmuLinkerList.size(); i++) {

            NonMoimingUserVO baseData = sessionActivity.unfinishedNmuLinkerList.get(i);

            SessionStatusMemberLinkerData nmuData = new SessionStatusMemberLinkerData(baseData);

            unfinishedNmuDataList.add(nmuData);

        }

    }
}
