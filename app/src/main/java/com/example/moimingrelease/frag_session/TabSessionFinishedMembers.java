package com.example.moimingrelease.frag_session;

import android.os.Bundle;
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
import com.example.moimingrelease.app_adapter.SessionFinishedMembersAdapter;
import com.example.moimingrelease.app_adapter.SessionNormalMemberAdapter;
import com.example.moimingrelease.app_adapter.SessionUnfinishedMembersAdapter;
import com.example.moimingrelease.app_adapter.SessionUnfinishedNmusAdapter;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.UserSessionLinkerVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionStatusMemberLinkerData;

import java.util.ArrayList;
import java.util.List;

public class TabSessionFinishedMembers extends Fragment {

    private TextView textMember1, textNmu1;

    private SessionActivity sessionActivity;
    private RecyclerView memberRecycler, nmuRecycler;

    // 한 Recycler 에는 한 Adapter 만 가능
    private SessionFinishedMembersAdapter memberAdapter;
    private SessionFinishedMembersAdapter nmuAdapter;

    private SessionNormalMemberAdapter normalMemberViewAdapter;
    private SessionNormalMemberAdapter normalMemberNmuViewAdapter;

    private List<SessionStatusMemberLinkerData> finishedMemberDataList;
    private List<SessionStatusMemberLinkerData> finishedNmuDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_session_finished_members, container, false);

        initView(view);

        initParams();

        initRecyclerView();

        return view;
    }

    private void initView(View view) {

        memberRecycler = view.findViewById(R.id.session_finished_member_status);
        nmuRecycler = view.findViewById(R.id.session_finished_nmu_status);

        textMember1 = view.findViewById(R.id.text_session_fin_member_1);
        textNmu1 = view.findViewById(R.id.text_session_fin_nmu_1);

    }

    private void initParams() {

        sessionActivity = (SessionActivity) getActivity();

        finishedMemberDataList = new ArrayList<>();
        finishedNmuDataList = new ArrayList<>();

    }

    public void initRecyclerView() {

        buildList();

        if (!sessionActivity.isSessionRefreshing) {

            LinearLayoutManager membersLayoutManager = new LinearLayoutManager(sessionActivity);

            memberRecycler.setLayoutManager(membersLayoutManager);


            LinearLayoutManager nmuLayoutManager = new LinearLayoutManager(sessionActivity);
            nmuRecycler.setLayoutManager(nmuLayoutManager);

            if (sessionActivity.curUserStatus != 0) { // 일반 멤버일경우

                textMember1.setVisibility(View.GONE);
                textNmu1.setVisibility(View.GONE);

                normalMemberViewAdapter = new SessionNormalMemberAdapter(sessionActivity.getApplicationContext(), finishedMemberDataList
                        , true, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession());
                memberRecycler.setAdapter(normalMemberViewAdapter);

                normalMemberNmuViewAdapter = new SessionNormalMemberAdapter(sessionActivity.getApplicationContext(), finishedNmuDataList
                        , false, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession());
                nmuRecycler.setAdapter(normalMemberNmuViewAdapter);


            } else { // 내가 총무일 경우

                textMember1.setVisibility(View.VISIBLE);
                textNmu1.setVisibility(View.VISIBLE);

                memberAdapter = new SessionFinishedMembersAdapter(sessionActivity.getApplicationContext(), finishedMemberDataList
                        , true, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession(), sessionActivity.sentUserCallback);
                memberRecycler.setAdapter(memberAdapter);

                nmuAdapter = new SessionFinishedMembersAdapter(sessionActivity.getApplicationContext(), finishedNmuDataList
                        , false, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession(), sessionActivity.sentUserCallback);
                nmuRecycler.setAdapter(nmuAdapter);

            }


            memberRecycler.addItemDecoration(new RecyclerViewItemDecoration(sessionActivity, 6));
            nmuRecycler.addItemDecoration(new RecyclerViewItemDecoration(sessionActivity, 6));

        } else {

            memberAdapter.notifyDataSetChanged();
            nmuAdapter.notifyDataSetChanged();
        }
    }

    private void buildList() {

        finishedMemberDataList.clear();
        finishedNmuDataList.clear();


        for (int i = 0; i < sessionActivity.finishedMemberLinkerList.size(); i++) {

            UserSessionLinkerVO baseData = sessionActivity.finishedMemberLinkerList.get(i);

            SessionStatusMemberLinkerData memberData = new SessionStatusMemberLinkerData(baseData.getMoimingUser(), baseData.getPersonalCost(), baseData.isSent());

            finishedMemberDataList.add(memberData);

        }


        for (int i = 0; i < sessionActivity.finishedNmuLinkerList.size(); i++) {

            NonMoimingUserVO baseData = sessionActivity.finishedNmuLinkerList.get(i);

            SessionStatusMemberLinkerData nmuData = new SessionStatusMemberLinkerData(baseData);

            finishedNmuDataList.add(nmuData);

        }


    }
}
