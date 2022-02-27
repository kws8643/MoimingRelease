package com.example.moimingrelease.frag_session_creation_previous_funding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionCreationActivity;
import com.example.moimingrelease.SessionCreationPreviousFundingActivity;
import com.example.moimingrelease.app_adapter.PreviousFundingMemberInfoAdapter;
import com.example.moimingrelease.app_adapter.PreviousFundingViewAdapter;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.GroupPaymentVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.UserSessionLinkerVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.PreviousSessionMemberStatusData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewFundingInfoFragment extends Fragment {

    SessionCreationPreviousFundingActivity activity;
    private MoimingSessionVO curSession;

    private Button btnLoadPrevFunding;

    private TextView textFundingCost, textFundingMemberCnt, textMoimingMemberCnt, textNmuCnt;
    private RecyclerView moimingMemberRecy, nmuRecy;

    private List<PreviousSessionMemberStatusData> memberRecyclerDataList;
    private List<PreviousSessionMemberStatusData> nmuRecyclerDataList;

    Comparator<UserSessionLinkerVO> byName = new Comparator<UserSessionLinkerVO>() {
        @Override
        public int compare(UserSessionLinkerVO linker1, UserSessionLinkerVO linker2) {
            return linker1.getMoimingUser().getUserName().compareTo(linker2.getMoimingUser().getUserName());
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            curSession = (MoimingSessionVO) getArguments().getSerializable(getResources().getString(R.string.moiming_session_data_key));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_prev_funding_view_info, container, false);

        initView(view);
        initParams();

        initRecyclerView();

        btnLoadPrevFunding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 해당 세션을 SessionCreationActivity 로 보내서, 그 상태 그대로 로딩 시킨다.
                Intent loadFundingIntent = new Intent();

                loadFundingIntent.putExtra(getResources().getString(R.string.moiming_session_data_key), (Serializable) curSession);

                activity.setResult(Activity.RESULT_OK, loadFundingIntent);

                activity.finish();
            }
        });

        return view;
    }

    private void initView(View view) {

        btnLoadPrevFunding = view.findViewById(R.id.btn_load_funding);

        textFundingCost = view.findViewById(R.id.text_funding_total_cost);
        String cost = curSession.getTotalCost() + " 원";
        textFundingCost.setText(cost);

        textFundingMemberCnt = view.findViewById(R.id.text_funding_member_cnt);
        String memberCnt = curSession.getSessionMemberCnt() + " 명";
        textFundingMemberCnt.setText(memberCnt);

        // 아래 RecyList 들
        textMoimingMemberCnt = view.findViewById(R.id.text_prev_moiming_cnt);
        textNmuCnt = view.findViewById(R.id.text_prev_nmu_cnt);

        moimingMemberRecy = view.findViewById(R.id.funding_info_moiming_recy);
        nmuRecy = view.findViewById(R.id.funding_info_nmu_recy);

    }

    private void initParams() {

        activity = (SessionCreationPreviousFundingActivity) getActivity();
        memberRecyclerDataList = new ArrayList<>();
        nmuRecyclerDataList = new ArrayList<>();

        List<UserSessionLinkerVO> usLinkerRawList = curSession.getUsLinkerList();
        Collections.sort(usLinkerRawList, byName);

        for (UserSessionLinkerVO linkerData : usLinkerRawList) { // 이름 순으로 sorting 된 상태
            if (linkerData.getMoimingUser().getUuid()
                    .equals(curSession.getSessionCreatorUuid())) { // 생성자는 1빠로 먼저 빼놓는다.

                PreviousSessionMemberStatusData creatorData = new PreviousSessionMemberStatusData(
                        linkerData.getMoimingUser().getUserName() + " (총무)"
                        , linkerData.getPersonalCost());
                memberRecyclerDataList.add(0, creatorData); // 제일 앞에 놓는다.

            } else if (linkerData.getMoimingUser().getUuid()
                    .equals(activity.getCurUser().getUuid())) { // 현재 유저일 경우

                PreviousSessionMemberStatusData userData = new PreviousSessionMemberStatusData(
                        linkerData.getMoimingUser().getUserName() + " (나)"
                        , linkerData.getPersonalCost());

                memberRecyclerDataList.add(userData);

            } else {

                PreviousSessionMemberStatusData userData = new PreviousSessionMemberStatusData(
                        linkerData.getMoimingUser().getUserName()
                        , linkerData.getPersonalCost());

                memberRecyclerDataList.add(userData);
            }
        }


        // NMU 는 그냥 냅다  박으면 됨.
        if (curSession.getNmuList() != null) {
            for (NonMoimingUserVO nmuVO : curSession.getNmuList()) {
                PreviousSessionMemberStatusData nmuData = new PreviousSessionMemberStatusData(
                        nmuVO.getNmuName()
                        , nmuVO.getNmuPersonalCost()
                );

                nmuRecyclerDataList.add(nmuData);
            }
        }

        String mmCnt = memberRecyclerDataList.size() + " 명";
        String nmuCnt = nmuRecyclerDataList.size() + " 명";

        textMoimingMemberCnt.setText(mmCnt);
        textNmuCnt.setText(nmuCnt);

    }

    private void initRecyclerView() {

        // 1. MoimingMember들 (여기엔 moiming 생성자 / 현재 유저 정보가 포함되어야 함)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        moimingMemberRecy.setLayoutManager(linearLayoutManager);

        PreviousFundingMemberInfoAdapter memberAdatper = new PreviousFundingMemberInfoAdapter(activity.getApplicationContext(), memberRecyclerDataList);
        moimingMemberRecy.setAdapter(memberAdatper);

        // 2.  NMU 들
        if (nmuRecyclerDataList.size() != 0) {
            LinearLayoutManager nmuLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
            nmuRecy.setLayoutManager(nmuLayoutManager);

            PreviousFundingMemberInfoAdapter nmuAdatper = new PreviousFundingMemberInfoAdapter(activity.getApplicationContext(), nmuRecyclerDataList);
            nmuRecy.setAdapter(nmuAdatper);
        }

    }
}
