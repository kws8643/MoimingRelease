package com.example.moimingrelease.frag_session_creation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.InviteSessionMembersInGroupActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_adapter.InviteSessionGroupMembersRecyclerAdapter;
import com.example.moimingrelease.app_adapter.KakaoFriendRecyclerAdapter;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;

import java.util.ArrayList;
import java.util.List;


public class TabSessionMembersInGroupFragment extends Fragment {

    InviteSessionMembersInGroupActivity parentActivity;

    private ConstraintLayout layoutKmfSearch, layoutGroupMemberSearch;
    private TextView btnSelectAll, textNoResult;
    private EditText searchMember;

    boolean isNewGroupCreation;

    RecyclerView groupMembersRecyclerView;
    InviteSessionGroupMembersRecyclerAdapter recyclerAdapter;

    List<MoimingMembersDTO> rawMembersList;
    List<MoimingMembersDTO> adapterMembersList;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = searchMember.getText().toString();

            searchMemberByText(text);

        }
    };

    public void userRemoveBtnClicked(String memberUuid) {

        recyclerAdapter.removeMember(memberUuid);
    }

    private void searchMemberByText(String text) {

        adapterMembersList.clear();

        if (text.length() == 0) { // 검색 결과가 안나올 경우

            adapterMembersList.addAll(rawMembersList);

        } else {

            for (int i = 0; i < rawMembersList.size(); i++) {

                MoimingMembersDTO member = rawMembersList.get(i);
                String userName = member.getUserName();

                if (userName.toLowerCase().contains(text)) {

                    adapterMembersList.add(member);
                }
            }
        }

        if (adapterMembersList.size() == 0) { //  검색된 사람이 없을 경우
            // 결과 없다고 텍스트 보여주면 될 듯
            textNoResult.setVisibility(View.VISIBLE);

        } else {
            textNoResult.setVisibility(View.GONE);
        }

        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_session_creation_in_group_group_member, container, false);

        initParams();

        initView(view);

        initRecyclerView();

        return view;
    }


    private void initParams() {

        parentActivity = (InviteSessionMembersInGroupActivity) getActivity();

        isNewGroupCreation = parentActivity.getFromNewGroupCreation();

        rawMembersList = new ArrayList<>();
        if (isNewGroupCreation) { // 새 더치페이 바로 생성

            rawMembersList = parentActivity.getKmfMembers();

        } else { // 일반 생성

            rawMembersList = parentActivity.getCurGroupMembers();
        }

        adapterMembersList = new ArrayList<>();
        adapterMembersList.addAll(rawMembersList);


    }

    private void initView(View view) {

        layoutGroupMemberSearch = view.findViewById(R.id.layout_group_member_search);
        layoutKmfSearch = view.findViewById(R.id.layout_kmf_search);

        textNoResult = view.findViewById(R.id.text_no_search_result);
        btnSelectAll = view.findViewById(R.id.btn_select_all_group_members);

        if (isNewGroupCreation) {
            btnSelectAll.setVisibility(View.GONE);
            layoutKmfSearch.setVisibility(View.VISIBLE);
            layoutGroupMemberSearch.setVisibility(View.GONE);

            searchMember = view.findViewById(R.id.input_kmf_name);

        } else {
            btnSelectAll.setVisibility(View.VISIBLE);

            layoutKmfSearch.setVisibility(View.GONE);
            layoutGroupMemberSearch.setVisibility(View.VISIBLE);

            searchMember = view.findViewById(R.id.input_group_member);
        }

        searchMember.addTextChangedListener(textWatcher);

        groupMembersRecyclerView = view.findViewById(R.id.create_session_group_members_list);

    }


    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity);
        groupMembersRecyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter = new InviteSessionGroupMembersRecyclerAdapter(parentActivity.getApplicationContext()
                , adapterMembersList, parentActivity.getCheckBoxListener(), parentActivity.getPreInvitedMembersUuid(), isNewGroupCreation);
        groupMembersRecyclerView.setAdapter(recyclerAdapter);

    }

}
