package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_adapter.MainRecyclerAdapter;
import com.example.moimingrelease.app_adapter.RecyclerViewItemDecoration;
import com.example.moimingrelease.app_adapter.SearchGroupAdapter;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.SearchedGroupViewData;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupActivity extends AppCompatActivity {


    /**
     * 가져와야 하는 정보들.
     * -->  해당 그룹과 해당 그룹 멤버들도 다 Source 로 가지고 있어야 함. (그룹 멤버들은 호출하진 않는데..)
     * <p>
     * --> 아무래도 백엔드 단을 수정하여, 초반에 그룹VO 들을 형성할 떄 본인 포함하는 GroupMembersDTO 도 다 형성시켜야 할 것 같음.
     * --> 그리고 GroupVO 로 같이 묶은 다음에 여기에 가져오는게 맞을 것 같음.
     * --> 일단 Group 과 멤버들 묶는 것은 성공. 좀 이상하긴 하지만.. 아니 Observable 이 좀 불완전한 동기 같음.
     */


    // 1. GroupActivity 로 부터 Group 과 각 GroupMembers 들의 정보를 가져오고, 그 정보들 안에서 뒤질 것임.
    // 대상 = GroupName, GroupMembersName

    private MoimingUserVO curUser;

    // 2. Recycler Adapter 와 연동하여 검색 알고리즘 만들면 될듯!
    private TextView textNoResult;
    private EditText inputSearch;
    private RecyclerView resultRecycler;
    private SearchGroupAdapter searchRecyclerAdapter;

    private ArrayList<MoimingGroupAndMembersDTO> userGroupAndMemebersList;

    // 검색 Main Holder
    private List<SearchedGroupViewData> searchedGroupDataList;


    private void receiveIntent() {

        Intent receivedData = getIntent();

        if (receivedData.getExtras() != null) {
            curUser = (MoimingUserVO) receivedData.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            userGroupAndMemebersList = receivedData.getParcelableArrayListExtra(getResources().getString(R.string.search_group_data_key));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        receiveIntent();

        initView();

        initParams();

        initRecyclerView();
        hideRecyclersAndShowText();

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO: 문자가 바뀔때마다 검색 알고리즘을 시행한다.
                String text = inputSearch.getText().toString();
                searchGroupAndMembers(text);


            }
        });
    }

    // TODO: Main METHOD ******************************
    private void searchGroupAndMembers(String text) {

        // 1. 문자 입력시마다 list 를 지운다
        searchedGroupDataList.clear();

        // 2. text 가 비어있을때는 최근 검색어를 다시 표시한다. (현재 개발 X) / (현재는 그냥 검색 결과 없다고 띄움)
        if (text.length() == 0) {

            hideRecyclersAndShowText();

        } else {

            showRecyclersAndHideText();

            // 3. 검색어가 검색 대상 중 groupName 에 있는지 확인한다.
            searchInGroupName(text);

            // 4. 각 그룹 멤버들을 검색해서 같은 이름이 있는지 확인한다. 있을 경우 isMember를 true로 한다.
            searchInGroupMembersName(text);

        }

        if (searchedGroupDataList.size() == 0) {
            hideRecyclersAndShowText();
        }
        searchRecyclerAdapter.notifyDataSetChanged();

    }

    private void initView() {

        textNoResult = findViewById(R.id.text_no_result);
        inputSearch = findViewById(R.id.input_search_text);

        resultRecycler = findViewById(R.id.search_group_result_recycler);

    }

    private void initParams() {

        searchedGroupDataList = new ArrayList<>();

    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        resultRecycler.setLayoutManager(linearLayoutManager);

        searchRecyclerAdapter = new SearchGroupAdapter(this, curUser, searchedGroupDataList);
        resultRecycler.setAdapter(searchRecyclerAdapter);

    }

    private void hideRecyclersAndShowText() {

        resultRecycler.setVisibility(View.GONE);
        textNoResult.setVisibility(View.VISIBLE);
    }

    private void showRecyclersAndHideText() {

        resultRecycler.setVisibility(View.VISIBLE);
        textNoResult.setVisibility(View.GONE);

    }


    // ArrayList<MoimingGroupAndMembersDTO> userGroupAndMemebersList;
    private void searchInGroupName(String text) {

        for (int i = 0; i < userGroupAndMemebersList.size(); i++) {

            MoimingGroupAndMembersDTO userGroupData = userGroupAndMemebersList.get(i);

            if (userGroupData.getMoimingGroup().getGroupName().toLowerCase().contains(text)) { // 해당 그룹이 검색 대상임.

                SearchedGroupViewData searchedGroupData = new SearchedGroupViewData(userGroupData, false, null);
                searchedGroupDataList.add(searchedGroupData);

            }
        }
    }

    private void searchInGroupMembersName(String text) {

        List<String> searchedGroupMembers = new ArrayList<>();

        for (int i = 0; i < userGroupAndMemebersList.size(); i++) {

            searchedGroupMembers.clear();

            boolean isAdded = false;

            MoimingGroupAndMembersDTO userGroupData = userGroupAndMemebersList.get(i);
            List<MoimingMembersDTO> membersList = userGroupData.getMoimingMembersList();

            for (int j = 0; j < membersList.size(); j++) {

                if (membersList.get(j).getUserName().toLowerCase().contains(text)) { // 검색되는 친구 모두 넣는다.

                    isAdded = true; // 검색되었으니 해당 그룹은 추가 대상임을 표시
                    searchedGroupMembers.add(membersList.get(j).getUserName());

                }
            }

            if (isAdded) { // 추가 대상인 그룹에 한해 추가를 한다
                SearchedGroupViewData searchedGroupData = new SearchedGroupViewData(userGroupData, true, searchedGroupMembers);
                searchedGroupDataList.add(searchedGroupData);
            }
        }
    }

}