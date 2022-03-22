package com.example.moimingrelease.fragments_main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.reactivex.rxjava3.annotations.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_adapter.MainRecyclerAdapter;
import com.example.moimingrelease.app_adapter.RecyclerViewItemDecoration;
import com.example.moimingrelease.app_listener_interface.MainFixedGroupRefreshListener;
import com.example.moimingrelease.app_listener_interface.GroupExitDialogListener;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    RecyclerView mainRecyclerView;
    MainRecyclerAdapter mainRecyclerAdapter;

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

            // 1. SP 를 다시 불러와서 FixedList 를 초기화한다.
            // 2. notify 실행으로 되려나..
            buildOrRefreshFixedGroupList();
            // TODO: SORTING 다시 해주면 됨.
            sortAdapterGroupData();

            mainRecyclerAdapter.notifyDataSetChanged();

        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_main_home, container, false);

        initView(view);

        initParams();

        getUgLinkers();

        return view;

    }


    private void initView(View view) {

        mainRecyclerView = view.findViewById(R.id.main_recycler_view);


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

                        Log.w("TAG", responseData.toString());

                        rawDataList.clear();

                        List<UGLinkerResponseDTO> groupLinkerLists = responseData.getData();

                        for (UGLinkerResponseDTO linker : groupLinkerLists) {  // TODO 1: map 에 모든 group 을 키로 넣는다

                            MoimingGroupVO moimingGroup = linker.getMoimingGroupResponseDTO().convertToVO();
                            mainActivity.rawNotificationMap.put(moimingGroup.getUuid(), new ArrayList<>());

                        }

                        for (int i = 0; i < mainActivity.rawNotificationList.size(); i++) { // TODO 2: notification 온 그룹이 있다면 그거에 따라 map 에 noti 를 추가

                            ReceivedNotificationDTO dto = mainActivity.rawNotificationList.get(i);

                            List<ReceivedNotificationDTO> value = mainActivity.rawNotificationMap.get(dto.getNotification().getSentGroupUuid());

                            value.add(dto);

                            mainActivity.rawNotificationMap.put(dto.getNotification().getSentGroupUuid(), value);

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

                        }

                        @Override
                        public void onComplete() {

                            if (index == rawDataList.size() - 1) { // 마지막 순서면,

                                if (MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED) {

                                    sortAdapterGroupData();

                                    mainRecyclerAdapter.notifyDataSetChanged();

                                    MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = false;

                                } else { // 처음일 경우

                                    initRecyclerView();

                                }

                            } else {

                                rawDataList.set(index, data); // rawData에 해당 index 에 끼워넣는다.

                            }
                        }
                    });

        }
    }

    public void applyRefreshedNotification(){

        for(int i =0 ; i < rawDataList.size() ; i++){ // Map 만들어 놓는다.

            MainRecyclerLinkerData rawSingleData = rawDataList.get(i);

            mainActivity.rawNotificationMap.put(rawSingleData.getGroupData().getMoimingGroup().getUuid(), new ArrayList<>());

        }

        for (int i = 0; i < mainActivity.rawNotificationList.size(); i++) { // TODO 2: 앞에서 Raw Notification Map 에 노티 하나씩 돌려가면서 맞는 위치에 넣어준다.

            ReceivedNotificationDTO dto = mainActivity.rawNotificationList.get(i);

            List<ReceivedNotificationDTO> value = mainActivity.rawNotificationMap.get(dto.getNotification().getSentGroupUuid()); // 쌓여있는 List 를 가져와서,

            value.add(dto); // 이번 dto 를 추가해주고,

            mainActivity.rawNotificationMap.put(dto.getNotification().getSentGroupUuid(), value); // set 해준다.

        }

        for(int i = 0; i < rawDataList.size(); i++){

            MainRecyclerLinkerData rawSingleData = rawDataList.get(i);

            rawSingleData.setGroupNotification(mainActivity.rawNotificationMap.get(rawSingleData.getGroupData().getMoimingGroup().getUuid()));

            rawDataList.set(i, rawSingleData); // 교체해준다.
        }

        sortAdapterGroupData();

        MainActivity.IS_NOTIFICATION_REFRESH_NEEDED = false;
        mainRecyclerAdapter.notifyDataSetChanged();
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


    // TODO: 알림을 활용한 Sorting 필요
    // Sorting 대상 RecyclerDataList // RawDataList 는 건드리지 않음.
    private void sortAdapterGroupData() {

        // 이것도 검색이랑 똑같음!
        recyclerDataList.clear();

        List<MainRecyclerLinkerData> tempDataList = new ArrayList<>();

        // 1. 고정 그룹은 앞으로 뺀다. (고정 그룹에서도 이름 순)
        for (int i = 0; i < rawDataList.size(); i++) {

            // 고정 그룹에 속해있는 uuid 라면! 먼저 추가한다.
            if (userFixedGroupList.contains(rawDataList.get(i).getGroupData().getMoimingGroup().getUuid().toString())) {
                recyclerDataList.add(rawDataList.get(i));
            } else {
                tempDataList.add(rawDataList.get(i));
            }
        }

        Collections.sort(recyclerDataList, byName); // 고정된 애들 이름순으로 소팅 먼저 해준다.

        Collections.sort(tempDataList, byName); // 남은 애들 이름순으로 소팅 한번 해주고
        recyclerDataList.addAll(tempDataList); // 순서대로 추가된다.


        // 3. TODO:추후에 알림이 도입되면 순서가 재정렬된다.


        tempDataList.clear();

    }


    // 한글이 먼저 오게
    Comparator<MainRecyclerLinkerData> byName = new Comparator<MainRecyclerLinkerData>() {
        @Override
        public int compare(MainRecyclerLinkerData data1, MainRecyclerLinkerData data2) {
            return data2.getGroupData().getMoimingGroup().getGroupName()
                    .compareTo(data1.getGroupData().getMoimingGroup().getGroupName());
        }
    };

    @Override
    public void onResume() { //TODO: 이런식으로 onResume 에 사용하는게 맞을까?
        super.onResume();

    }
}
