package com.example.moimingrelease.frag_session;

import android.os.Bundle;
import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.kakao.sdk.link.LinkClient;
import com.kakao.sdk.link.model.LinkResult;
import com.kakao.sdk.template.model.Button;
import com.kakao.sdk.template.model.Content;
import com.kakao.sdk.template.model.FeedTemplate;
import com.kakao.sdk.template.model.Link;
import com.kakao.sdk.template.model.Social;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class TabSessionUnFinishedMembers extends Fragment {

    private TextView textMemberRef, textNmuRef;
    private TextView textMemberCheckBoxRef, textNmuCheckBoxRef;
    private ConstraintLayout btnSendAllMembers;
    private ConstraintLayout btnSendKakao;

    private SessionActivity sessionActivity;
    private RecyclerView memberRecycler, nmuRecycler;
    private SessionUnfinishedMembersAdapter membersAdapter;
    private SessionUnfinishedNmusAdapter nmusAdapter;

    private SessionNormalMemberAdapter normalMemberViewAdapter;
    private SessionNormalMemberAdapter normalMemberNmuViewAdapter;

    private List<SessionStatusMemberLinkerData> unfinishedMemberDataList;
    private List<SessionStatusMemberLinkerData> unfinishedNmuDataList;


    public void requestAdapterConfirmCheckedUser() {

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

                // TODO: 다시 누를 수 없게 뷰적으로 만들기
                sessionActivity.sendFinishFCMRequest(true, null, 0);
                btnSendAllMembers.setEnabled(false);

            }
        });


        btnSendKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openAndSendKakaoTemplate();
            }
        });


        return view;
    }

    private FeedTemplate buildFeedTemplate() {

        List<Button> btnList = new ArrayList<>();

        btnList.add(new Button("웹에서 보기", new Link("https://developers.kakao.com"
                , "https://developers.kakao.com")));

        FeedTemplate feed = new FeedTemplate(new Content("뭐 넣으면 좋을지 고민해봅시다.", "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg"
                , new Link("https://developers.kakao.com", "https://developers.kakao.com"), "꼭 이런거 아니여도 됩니다. 카카오톡 정산 요청 부분!")
                , new Social(10, 20, 30, 40)
                , btnList);

        return feed;
    }

    private void openAndSendKakaoTemplate() {

        FeedTemplate feedTemp = buildFeedTemplate();

        if (LinkClient.getInstance().isKakaoLinkAvailable(sessionActivity)) {

            LinkClient.getInstance().defaultTemplate(sessionActivity, feedTemp, new Function2<LinkResult, Throwable, Unit>() {
                @Override
                public Unit invoke(LinkResult linkResult, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else if (linkResult != null) {

                        Toast.makeText(sessionActivity.getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                        startActivity(linkResult.getIntent());

                    }

                    return null;
                }
            });

        } else {


            Toast.makeText(sessionActivity.getApplicationContext(), "불가", Toast.LENGTH_SHORT).show();
        }

    }

    private void initView(View view) {

        memberRecycler = view.findViewById(R.id.session_moiming_member_status);
        nmuRecycler = view.findViewById(R.id.session_nmu_member_status);

        textMemberCheckBoxRef = view.findViewById(R.id.text_session_member_1);
        textMemberRef = view.findViewById(R.id.text_moiming_members);
        textNmuRef = view.findViewById(R.id.text_nmu_members);
        textNmuCheckBoxRef = view.findViewById(R.id.text_nmu_ref_1);

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

        if (!sessionActivity.isSessionRefreshing) {
            LinearLayoutManager membersLayoutManager = new LinearLayoutManager(sessionActivity);
            memberRecycler.setLayoutManager(membersLayoutManager);


            LinearLayoutManager nmuLayoutManager = new LinearLayoutManager(sessionActivity);
            nmuRecycler.setLayoutManager(nmuLayoutManager);

            if (sessionActivity.curUserStatus != 0) { // 일반 멤버일경우 // TODO: 내 상태 전달 필요

                textMemberCheckBoxRef.setVisibility(View.GONE);
                btnSendAllMembers.setVisibility(View.GONE);
                btnSendKakao.setVisibility(View.GONE);

                normalMemberViewAdapter = new SessionNormalMemberAdapter(sessionActivity.getApplicationContext()
                        , unfinishedMemberDataList, true, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession());
                memberRecycler.setAdapter(normalMemberViewAdapter);

                normalMemberNmuViewAdapter = new SessionNormalMemberAdapter(sessionActivity.getApplicationContext()
                        , unfinishedNmuDataList, false, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession());
                nmuRecycler.setAdapter(normalMemberNmuViewAdapter);


            } else { // 내가 총무일 경우
                if (textMemberCheckBoxRef.getVisibility() == View.VISIBLE) {
                    textMemberCheckBoxRef.setVisibility(View.VISIBLE);
                } else {
                    textMemberCheckBoxRef.setVisibility(View.GONE);
                }
                if (btnSendAllMembers.getVisibility() == View.VISIBLE) {
                    btnSendAllMembers.setVisibility(View.VISIBLE);
                } else {
                    btnSendAllMembers.setVisibility(View.GONE);
                }
                if (btnSendKakao.getVisibility() == View.VISIBLE) {
                    btnSendKakao.setVisibility(View.VISIBLE);
                } else {
                    btnSendKakao.setVisibility(View.GONE);
                }

                membersAdapter = new SessionUnfinishedMembersAdapter(sessionActivity.getApplicationContext()
                        , unfinishedMemberDataList, sessionActivity.getCurUserUuid(), sessionActivity.getCurSession()
                        , sessionActivity.checkBoxCallback);
                memberRecycler.setAdapter(membersAdapter);

                nmusAdapter = new SessionUnfinishedNmusAdapter(sessionActivity.getApplicationContext(), unfinishedNmuDataList, sessionActivity.nmuCallback);
                nmuRecycler.setAdapter(nmusAdapter);
            }


        } else {

            membersAdapter.notifyDataSetChanged();
            nmusAdapter.notifyDataSetChanged();

        }
    }


    private void buildLists() {

        unfinishedNmuDataList.clear();
        unfinishedMemberDataList.clear();

        if (sessionActivity.unfinishedMemberLinkerList.size() > 0) {

            textMemberRef.setVisibility(View.VISIBLE);
            textMemberCheckBoxRef.setVisibility(View.VISIBLE);
            textNmuCheckBoxRef.setVisibility(View.INVISIBLE);
            btnSendAllMembers.setVisibility(View.VISIBLE);

            for (int i = 0; i < sessionActivity.unfinishedMemberLinkerList.size(); i++) {

                UserSessionLinkerVO baseData = sessionActivity.unfinishedMemberLinkerList.get(i);

                SessionStatusMemberLinkerData memberData = new SessionStatusMemberLinkerData(baseData.getMoimingUser()
                        , baseData.getPersonalCost(), baseData.isSent());

                unfinishedMemberDataList.add(memberData);

            }
        } else {

            btnSendAllMembers.setVisibility(View.GONE);
            textMemberRef.setVisibility(View.GONE);
            textMemberCheckBoxRef.setVisibility(View.GONE);
            textNmuCheckBoxRef.setVisibility(View.VISIBLE);
        }


        if (sessionActivity.unfinishedNmuLinkerList.size() > 0) {
            textNmuRef.setVisibility(View.VISIBLE);
            btnSendKakao.setVisibility(View.VISIBLE);
            for (int i = 0; i < sessionActivity.unfinishedNmuLinkerList.size(); i++) {

                NonMoimingUserVO baseData = sessionActivity.unfinishedNmuLinkerList.get(i);

                SessionStatusMemberLinkerData nmuData = new SessionStatusMemberLinkerData(baseData);

                unfinishedNmuDataList.add(nmuData);

            }
        } else { // 0일 때, 즉 없을 때

            btnSendKakao.setVisibility(View.GONE);
            textNmuRef.setVisibility(View.GONE);

        }
    }

    public void allBtnChangeToAdapter() {

        if (membersAdapter != null) {

            membersAdapter.changeAllRequestBtn();
        }
    }
}
