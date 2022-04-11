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
import com.example.moimingrelease.app_adapter.SessionFinishedAdapter;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.UserSessionLinkerVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionStatusMemberLinkerData;

import java.util.ArrayList;
import java.util.List;

public class TabFinishedSession extends Fragment {

    private SessionActivity activity;

    private RecyclerView nmuRecycler, memberRecycler;
    private TextView nmuText;

    private SessionFinishedAdapter memberRecyclerAdapter;
    private SessionFinishedAdapter nmuRecyclerAdapter;

    private List<SessionStatusMemberLinkerData> memberDataList;
    private List<SessionStatusMemberLinkerData> nmuDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_session_finished, container, false);

        initView(view);

        initParams();

        initRecyclerView();

        return view;
    }


    private void initView(View view) {

        memberRecycler = view.findViewById(R.id.session_finished_member);
        nmuRecycler = view.findViewById(R.id.session_finished_nmu);

        nmuText = view.findViewById(R.id.text_session_finished_nmu);

    }

    private void initParams() {

        activity = (SessionActivity) getActivity();

        memberDataList = new ArrayList<>();
        nmuDataList = new ArrayList<>();

    }

    private void initRecyclerView() {

        buildList();

        LinearLayoutManager membersLayoutManager = new LinearLayoutManager(activity);
        memberRecycler.setLayoutManager(membersLayoutManager);

        memberRecyclerAdapter = new SessionFinishedAdapter(activity.getApplicationContext(), true
                , activity.getCurSession(), activity.getCurUserUuid(), memberDataList);
        memberRecycler.setAdapter(memberRecyclerAdapter);


        if (nmuDataList.size() > 0) {
            LinearLayoutManager nmuLayoutManager = new LinearLayoutManager(activity);
            nmuRecycler.setLayoutManager(nmuLayoutManager);

            nmuRecyclerAdapter = new SessionFinishedAdapter(activity.getApplicationContext(), false
                    , activity.getCurSession(), activity.getCurUserUuid(), nmuDataList);

            nmuRecycler.setAdapter(nmuRecyclerAdapter);
        }

    }

    private void buildList() {

        memberDataList.clear();
        nmuDataList.clear();


        for (int i = 0; i < activity.finishedMemberLinkerList.size(); i++) {

            UserSessionLinkerVO baseData = activity.finishedMemberLinkerList.get(i);

            SessionStatusMemberLinkerData memberData = new SessionStatusMemberLinkerData(baseData.getMoimingUser(), baseData.getPersonalCost(), baseData.isSent());

            memberDataList.add(memberData);

        }


        if (activity.finishedNmuLinkerList.size() > 0) {

            nmuText.setVisibility(View.VISIBLE);

            for (int i = 0; i < activity.finishedNmuLinkerList.size(); i++) {

                NonMoimingUserVO baseData = activity.finishedNmuLinkerList.get(i);

                SessionStatusMemberLinkerData nmuData = new SessionStatusMemberLinkerData(baseData);

                nmuDataList.add(nmuData);

            }
        } else {

            nmuText.setVisibility(View.GONE);
        }

    }

}
