package com.example.moimingrelease.fragments_main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import io.reactivex.rxjava3.annotations.NonNull;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moimingrelease.GroupActivity;
import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_adapter.MainRecyclerAdapter;
import com.example.moimingrelease.app_adapter.RecyclerViewItemDecoration;
import com.example.moimingrelease.app_listener_interface.MainFixedGroupRefreshListener;
import com.example.moimingrelease.app_listener_interface.GroupExitDialogListener;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.extras.SessionAndUserStatusDTO;
import com.example.moimingrelease.moiming_model.extras.UserGroupUuidDTO;
import com.example.moimingrelease.moiming_model.recycler_view_model.MainRecyclerLinkerData;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.response_dto.UGLinkerResponseDTO;
import com.example.moimingrelease.moiming_model.dialog.GroupExitConfirmDialog;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupRetrofitService;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.UGLinkerRetrofitService;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoimingHomeFragment extends Fragment {

    /**
     * 1. 현 유저의 uuid 를 전달해, 서버에서 해당 유저의 UgLinker 들을 전송한다.
     * 2. 서버에서 가져온 UgLinker 중 MoimingGroupVO에 대한 데이터도 존재한다. 해당 정보를 지속적으로 돌리게 됨.
     */

    /**
     * 처음에 어떤 sp 에 저장된 uuid 들을  array list 로 만들어 놓고, Adapter 에 해당 리스트를 전달하여 알아서 사용할 수 있도록 한다.
     * 하지만 상단 고정 등이 수행될 경우 refresh 를 하여야 한다.
     */

    private MainActivity mainActivity;

    private MoimingUserVO curUser;

    // 해당 리스트에는 각 그룹에 대한 정보와, 해당 그룹에서의 그룹 멤버 정보가 들어있다.
    private List<MainRecyclerLinkerData> recyclerDataList;
    private List<MainRecyclerLinkerData> rawDataList;

    private ArrayList<MoimingGroupAndMembersDTO> groupAndMemberDataList; // 해당 정보로 위에 recyclerDataList 만듦

    private ArrayList<String> userFixedGroupList;

    SharedPreferences fixedSp;
    public RecyclerView mainRecyclerView;
    private ScrollView notiScroll;
    MainRecyclerAdapter mainRecyclerAdapter;

    private SwipeRefreshLayout layoutSwipe;

    public void setAdapterRecyclerClickable(boolean isClickable) {

        if (isClickable) {

            mainRecyclerAdapter.setRecyclerClickable(true);

        } else {
            mainRecyclerAdapter.setRecyclerClickable(false);
        }

    }

    public ArrayList<MoimingGroupAndMembersDTO> getGroupAndMemberDataList() {

        return this.groupAndMemberDataList;
    }

    //TODO 이거랑 그룹 연결 해제 시, SP 에서 제거
    //  --> 또한 처음에 받아왔을때 속한 그룹에 없는 그룹은 SP에서 제거해주면 좋을듯?

    GroupExitDialogListener exitDialogListener = new GroupExitDialogListener() {
        @Override
        public void initExitConfirmDialog(boolean isConfirmed, String groupUuid) {

            // 다이얼로그 소환!
            if (!isConfirmed) {
                GroupExitConfirmDialog exitDialog = new GroupExitConfirmDialog(mainActivity, this, groupUuid);
                exitDialog.show();

            } else { // 위에 다이얼로그에서 확인해서 진짜 제거하는 부분.


                UGLinkerRetrofitService retrofit = GlobalRetrofit.getInstance().getRetrofit().create(UGLinkerRetrofitService.class);
                UserGroupUuidDTO sendChunk = new UserGroupUuidDTO(curUser.getUuid(), UUID.fromString(groupUuid));
                TransferModel<UserGroupUuidDTO> requestModel = new TransferModel<>(sendChunk);

                retrofit.deleteGroupFromUser(requestModel)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<TransferModel>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull TransferModel transferModel) {

                                int num = transferModel.getResultCode();

                                if (num != 500) {
                                    Toast.makeText(mainActivity.getApplicationContext(), "제거되었습니다.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e.getMessage() != null) {
                                    Log.e(MainActivity.MAIN_TAG, e.getMessage());
                                }
                            }

                            @Override
                            public void onComplete() {

                                // REFERSH 가 필요.
                                // 1. rawData 를 다시 받아오고,

                                MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = true;

                                // 해당 과정들을 통해서 재배열 되는 ArrayList 들을 다 초기화시켜준다.
                                groupAndMemberDataList.clear();
                                rawDataList.clear();

//                                getUgLinkers();

                            }
                        });

            }
        }
    };

    MainFixedGroupRefreshListener refreshListener = new MainFixedGroupRefreshListener() {
        @Override
        public void refreshMainGroup() {

            buildOrRefreshFixedGroupList();

            sortAdapterGroupData();

            mainRecyclerAdapter.alertItemList(recyclerDataList, userFixedGroupList);
            mainRecyclerAdapter.notifyDataSetChanged();
            mainRecyclerView.scrollToPosition(0);

        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_main_home, container, false);

        initView(view);

        initParams();

        getUgLinkers();

        layoutSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mainActivity.refreshBySwipe();

                layoutSwipe.setRefreshing(false);
            }
        });

        return view;

    }


    private void initView(View view) {

        mainRecyclerView = view.findViewById(R.id.main_recycler_view);

        notiScroll = view.findViewById(R.id.noti_main_scroll);

        layoutSwipe = view.findViewById(R.id.layout_swipe_main);

    }

    private void initParams() {

        mainActivity = (MainActivity) getActivity();

        curUser = mainActivity.curMoimingUser;
        recyclerDataList = new ArrayList<>();
        rawDataList = new ArrayList<>();

        groupAndMemberDataList = new ArrayList<>();

        fixedSp = getContext().getSharedPreferences(MainActivity.FIXED_GROUP_SP_NAME, Context.MODE_PRIVATE);

        userFixedGroupList = new ArrayList<>();
        buildOrRefreshFixedGroupList();

    }

    private void buildOrRefreshFixedGroupList() {

        String jsonUuidString = fixedSp.getString(MainActivity.FIXED_GROUP_UUID_KEY, "");
        userFixedGroupList.clear();

        if (!jsonUuidString.equals("")) {// 저장된 애들이 없으면 필요 없음.

            try {

                JSONArray jsonArray = new JSONArray(jsonUuidString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    userFixedGroupList.add(jsonArray.optString(i));
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

    }

    // User Group Linker 로 속한 그룹들을 모두 불러온다. 이 때 앞서 가지고 있던 Notification 정보들과 우선 엮어준
    public void getUgLinkers() {

        String userUuid = curUser.getUuid().toString();

        UGLinkerRetrofitService ugLinkerRetrofitService = GlobalRetrofit.getInstance().getRetrofit().create(UGLinkerRetrofitService.class);
        ugLinkerRetrofitService.getUgLinkers(userUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<UGLinkerResponseDTO>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<UGLinkerResponseDTO>> responseData) {

                        Log.e("Working Log 14: ", "WORKS!");

                        rawDataList.clear();

                        List<UGLinkerResponseDTO> groupLinkerLists = responseData.getData();

                        for (UGLinkerResponseDTO linker : groupLinkerLists) {  // TODO 1: map 에 모든 group 을 키로 넣는다

                            MoimingGroupVO moimingGroup = linker.getMoimingGroupResponseDTO().convertToVO();
                            mainActivity.rawNotificationMap.put(moimingGroup.getUuid(), new ArrayList<>());

                        }

                        for (int i = 0; i < mainActivity.rawNotificationList.size(); i++) { // TODO 2: notification 온 그룹이 있다면 그거에 따라 map 에 noti 를 추가

                            ReceivedNotificationDTO dto = mainActivity.rawNotificationList.get(i);

                            if (!dto.getNotification().getSentActivity().equals("system")) {
                                List<ReceivedNotificationDTO> value = mainActivity.rawNotificationMap.get(dto.getNotification().getSentGroupUuid());
                                value.add(dto);
                                mainActivity.rawNotificationMap.put(dto.getNotification().getSentGroupUuid(), value);
                            }
                        }

                        for (UGLinkerResponseDTO linker : groupLinkerLists) {  // TODO 3: Recycler 준비

                            MoimingGroupVO moimingGroup = linker.getMoimingGroupResponseDTO().convertToVO();
                            MoimingGroupAndMembersDTO groupData = new MoimingGroupAndMembersDTO(moimingGroup, null); //그룹 멤버 객체를 생성하고, 그룹을 담아 놓는다.

                            MainRecyclerLinkerData recyclerData = new MainRecyclerLinkerData(groupData
                                    , mainActivity.rawNotificationMap.get(moimingGroup.getUuid())); // 생성한 객체를 Recycler List 에 추가를 우선 해놓는다.

                            rawDataList.add(recyclerData);
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            Log.e(MainActivity.MAIN_TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                        buildGroupMembers(); // 멤버들을 엮기 위해서 전달. rawData Clear 이기 때문에 해야함?

//                        sortAdapterGroupData();
//                        mainRecyclerAdapter.notifyDataSetChanged();

                    }

                });
    }

    // 각 그룹마다의 멤버를 담고 있다.
    private void buildGroupMembers() {

        groupAndMemberDataList.clear(); // 이 함수가 다시 실행되는건 전체 정보 수정

        if (rawDataList.size() == 0) {

            if (mainActivity.processDialog.isShowing()) {
                mainActivity.processDialog.finish();
            }

            initRecyclerView();
        }

        for (int i = 0; i < rawDataList.size(); i++) {

            MainRecyclerLinkerData data = rawDataList.get(i);
            int index = i;

            GroupRetrofitService groupRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupRetrofitService.class);

            groupRetrofit.getGroupMembersList(data.getGroupData().getMoimingGroup().getUuid().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TransferModel<List<MoimingMembersDTO>>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull TransferModel<List<MoimingMembersDTO>> transferModel) {


                            List<MoimingMembersDTO> groupMemberData = transferModel.getData();

                            MoimingGroupAndMembersDTO groupData = data.getGroupData();
                            groupData.setMoimingMembersList(groupMemberData);
                            // 해당 그룹 데이터에 받아온 그룹 데이터를 추가해준다.

                            groupAndMemberDataList.add(groupData); // 다 완성된 다음에 데이터 전송용에 추가

                            data.setGroupData(groupData); // 리사이클러 용에 추가해준다.

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                            if (e.getMessage() != null) {

                                Log.e("GroupMemberBuildingError:: ", e.getMessage());
                            }

                        }

                        @Override
                        public void onComplete() {

                            if (mainActivity.processDialog.isShowing()) {
                                mainActivity.processDialog.finish();
                            }


                            if (index == rawDataList.size() - 1) { // 마지막 순서면,

                                if (MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED) {

                                    sortAdapterGroupData();

                                    mainRecyclerAdapter.notifyDataSetChanged();

                                    MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = false;

                                } else { // 처음일 경우

                                    initRecyclerView();

                                    checkMovingUuid(); // 만약 그룹 혹은 설정으로 이동시켜야 하는 경우

                                }

                            } else {

                                rawDataList.set(index, data); // rawData에 해당 index 에 끼워넣는다.

                            }
                        }
                    });

        }
    }

    public void applyRefreshedNotification() {

        for (int i = 0; i < rawDataList.size(); i++) { // Map 만들어 놓는다.

            MainRecyclerLinkerData rawSingleData = rawDataList.get(i);

            mainActivity.rawNotificationMap.put(rawSingleData.getGroupData().getMoimingGroup().getUuid(), new ArrayList<>());

        }

        // System 노티 때문에 에러 발생

        // 받은 노티가 있어서 돌리는데,
        for (int i = 0; i < mainActivity.rawNotificationList.size(); i++) { // TODO 2: 앞에서 Raw Notification Map 에 노티 하나씩 돌려가면서 맞는 위치에 넣어준다.

            // dto 는 시스템 공지인데,
            ReceivedNotificationDTO dto = mainActivity.rawNotificationList.get(i);

            if (!dto.getNotification().getSentActivity().equals("system")) {
                // 시스템 노티 이므로 해당하는 group 이 없어서, value 객체가 null
                List<ReceivedNotificationDTO> value = mainActivity.rawNotificationMap.get(dto.getNotification().getSentGroupUuid()); // 쌓여있는 List 를 가져와서,

                value.add(dto); // 이번 dto 를 추가해주고,

                mainActivity.rawNotificationMap.put(dto.getNotification().getSentGroupUuid(), value); // set 해준다.
            }
        }

        for (int i = 0; i < rawDataList.size(); i++) {

            MainRecyclerLinkerData rawSingleData = rawDataList.get(i);

            rawSingleData.setGroupNotification(mainActivity.rawNotificationMap.get(rawSingleData.getGroupData().getMoimingGroup().getUuid()));

            rawDataList.set(i, rawSingleData); // 교체해준다.
        }

        sortAdapterGroupData();
        mainRecyclerAdapter.alertItemList(recyclerDataList, userFixedGroupList);

        MainActivity.IS_NOTIFICATION_REFRESH_NEEDED = false;
        mainRecyclerAdapter.notifyDataSetChanged();

        if (mainActivity.processDialog.isShowing()) {
            mainActivity.processDialog.finish();
        }
    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        mainRecyclerView.setLayoutManager(linearLayoutManager);

        sortAdapterGroupData();

        mainRecyclerAdapter = new MainRecyclerAdapter(mainActivity, recyclerDataList
                , curUser, exitDialogListener, refreshListener, userFixedGroupList);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);

//        mainRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(mainActivity, 8));
    }


    // 정렬 순서
    // TODO:
    // 1. 고정 애들은 따로 빼놓는다.
    // 2. 고정 내에서 알림 있는 애들을 위로 올려준다. 나머지 고정 넣어준다.
    // 3. 고정 없는 애들 중에서 알림 있는 애들을 위로 올려준다. 나머지 고정 없는 애들 넣어준다.
    // 4. 나머지 넣어줄 때는 이름 소팅 한번씩 해준다.
    // 5. 필요한 함수 => 어떤 List 넣어서 알림 있는 애들과 없는 애들로 나누어 주는 함수
    //    이름 소팅해주는 함수. 한글은 뒤에 있어서 한글을 위에 있게 하려면 역소팅 한번, 각각끼리 정소팅 한번 해줘야 함.
    private void sortAdapterGroupData() {

        // 이것도 검색이랑 똑같음!
        recyclerDataList.clear();

        List<MainRecyclerLinkerData> tempDataList = new ArrayList<>(); // 고정 아닌 애들

        // 1. 고정 그룹은 앞으로 뺀다. (고정 그룹에서도 이름 순)
        for (int i = 0; i < rawDataList.size(); i++) {

            // 고정 그룹에 속해있는 uuid 라면! 먼저 추가한다.
            if (userFixedGroupList.contains(rawDataList.get(i).getGroupData().getMoimingGroup().getUuid().toString())) {
                recyclerDataList.add(rawDataList.get(i));
            } else {
                tempDataList.add(rawDataList.get(i));
            }
        }

        // TODO: 여기까지 고정인애들: recyclerDataList // 고정 아닌애들: tempDataList
        //      2. 고정인 내에서 알림있는 애들을 위로 올려준다.

        recyclerDataList = sortByNotification(recyclerDataList); // 정렬해준다.
        tempDataList = sortByNotification(tempDataList);

        recyclerDataList.addAll(tempDataList); // 고정인 애들 뒤로 고정 아닌애들을 넣어준다. (끝)

        tempDataList.clear(); // 사용한 것을 비워준다.

    }

    private List<MainRecyclerLinkerData> sortByNotification(List<MainRecyclerLinkerData> givenList) {

        List<MainRecyclerLinkerData> hasNotiGroupList = new ArrayList<>();

        for (int i = 0; i < givenList.size(); i++) {
            // 한 Data 에서, 이 Data 가 안읽은 알림이 있다 하면 바로 맨 위로.
            boolean isUnread = false;
            List<ReceivedNotificationDTO> groupNotiList = givenList.get(i).getGroupNotification();

            for (int j = 0; j < groupNotiList.size(); j++) {
                if (!groupNotiList.get(j).getNotification().getRead()) { // Read 가 false 인 애를 발견할 시
                    isUnread = true;
                    break; // 발견할시 깬다.
                }
            }

            if (isUnread) { // unread 가 있을시 hasNoti 로 추가하고, 제거한다.

                hasNotiGroupList.add(givenList.get(i));
                givenList.remove(i);
                i--;

            }
        }

        // 다 돌고 나면 hasGroupList 에 신규노티 있는애들, givenList 에 신규노티 없는 애들

        // TODO: givenList 알림 순으로 정렬, 알림 없으면 밖으로 뺀다.
        givenList = sortByNotificationDate(givenList);
        hasNotiGroupList.addAll(givenList); // hasNoti 뒤쪽으로 givenList 를 넣어준다.

        return hasNotiGroupList;

    }

    private List<MainRecyclerLinkerData> sortByNotificationDate(List<MainRecyclerLinkerData> givenList) {

        List<MainRecyclerLinkerData> tempNoNotiList = new ArrayList<>(); // 알림 없는 애들.

        for (int i = 0; i < givenList.size(); i++) {

            MainRecyclerLinkerData groupData = givenList.get(i);
            if (groupData.getGroupNotification().isEmpty()) { // 노티가 없을 때

                tempNoNotiList.add(groupData);
                givenList.remove(i);
                i--;
            }
        }

        for (int i = 0; i < givenList.size(); i++) { // 각 노티있는 애들을 노티 최신 순서대로 해준다
            List<ReceivedNotificationDTO> notiList = givenList.get(i).getGroupNotification();
            Collections.sort(notiList, byDateNotiList);
            givenList.get(i).setGroupNotification(notiList); // 정렬된 것으로 세팅해준다
        }

        Collections.sort(givenList, byRecentNoti); // 알림 있는애들은 최근 노티 순서로 바꾼다
        Collections.sort(tempNoNotiList, byCreated);// 알림 없는 애들도 그룹 생성일자 순으로 정렬해준다


        givenList.addAll(tempNoNotiList);

        return givenList;
    }

    Comparator<MainRecyclerLinkerData> byCreated = new Comparator<MainRecyclerLinkerData>() {
        @Override
        public int compare(MainRecyclerLinkerData dto1, MainRecyclerLinkerData dto2) {
            return dto2.getGroupData().getMoimingGroup().getCreatedAt().compareTo(dto1.getGroupData().getMoimingGroup().getCreatedAt());
        }
    };


    Comparator<ReceivedNotificationDTO> byDateNotiList = new Comparator<ReceivedNotificationDTO>() {
        @Override
        public int compare(ReceivedNotificationDTO dto1, ReceivedNotificationDTO dto2) {
            return dto2.getNotification().getCreatedAtForm().compareTo(dto1.getNotification().getCreatedAtForm());
        }
    };

    Comparator<MainRecyclerLinkerData> byRecentNoti = new Comparator<MainRecyclerLinkerData>() {
        @Override
        public int compare(MainRecyclerLinkerData dto1, MainRecyclerLinkerData dto2) {

            LocalDateTime date1 = dto1.getGroupNotification().get(0).getNotification().getCreatedAtForm();
            LocalDateTime date2 = dto2.getGroupNotification().get(0).getNotification().getCreatedAtForm();

            return date2.compareTo(date1);
        }
    };


    // TODO: 이동하라고 Order 받았을 경우
    private void checkMovingUuid() {

        if (mainActivity.getMovingGroupUuid().length() != 0) {

            MoimingGroupAndMembersDTO targetGroupData = null;
            for (int i = 0; i < recyclerDataList.size(); i++) {

                MainRecyclerLinkerData data = recyclerDataList.get(i);

                if (mainActivity.getMovingGroupUuid().equals(data.getGroupData().getMoimingGroup().getUuid().toString())) {
                    targetGroupData = data.getGroupData();
                }
            }

            if (targetGroupData != null) {

                Intent toGroupActivity = new Intent(mainActivity, GroupActivity.class);

                toGroupActivity.putExtra(mainActivity.getResources().getString(R.string.moiming_group_and_members_data_key), targetGroupData);
                toGroupActivity.putExtra(mainActivity.getResources().getString(R.string.moiming_user_data_key), curUser);
                toGroupActivity.putExtra(mainActivity.getResources().getString(R.string.group_move_to_session_key), mainActivity.getMovingSessionUuid());

                startActivity(toGroupActivity);

            } else {

                Toast.makeText(mainActivity.getApplicationContext(), "이동하시려는 그룹을 찾을 수 없습니다", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onResume() { //TODO: 이런식으로 onResume 에 사용하는게 맞을까?
        super.onResume();

    }
}
