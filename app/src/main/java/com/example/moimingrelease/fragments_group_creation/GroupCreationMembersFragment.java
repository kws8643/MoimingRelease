package com.example.moimingrelease.fragments_group_creation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moimingrelease.GroupActivity;
import com.example.moimingrelease.GroupCreationActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.request_dto.UGLinkerRequestDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.UGLinkerRetrofitService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GroupCreationMembersFragment extends Fragment {

    GroupCreationActivity groupCreationActivity;

    private ImageView btnBack;
    private Button btnFinish;

    // 전달될 Group 객체 정보.
    private String groupName;
    private String groupInfo;
    private MoimingGroupVO createdGroup;

    // 필요한 List 들
    private List<MoimingUserVO> groupMembers;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            groupName = getArguments().getString("group_name");
            groupInfo = getArguments().getString("group_info");

            Log.w(GroupCreationActivity.GROUP_CREATION_TAG, groupName + ", " + groupInfo);

        } else {
            Toast.makeText(groupCreationActivity.getApplicationContext(), "통신 오류 발생", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_group_creation_members, container, false);

        initView(view);

        initParams();

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 1. 체크 된 친구들을 모두 groupMembers 리스트로 만든다.
                // 1-1. 나의 UUID 를 포함 친구들의 uuid 를 가져올 수 있도록 한다. (Kakao -> MoimingUser -> getUUID)
                createGroupMembers();

                // 2. 만들고자 하는 그룹을 생성하여 DB에 저장한다.
                // 3. 체크된 친구들(현유저 포함)을 하나하나 그룹과 연결해준다.
                createCurGroup();


            }
        });

        return view;
    }

    private void initView(View view) {

        btnBack = view.findViewById(R.id.btn_back_group_creation);
        btnFinish = view.findViewById(R.id.btn_finish_group_creation);

    }

    private void initParams() {

        groupCreationActivity = (GroupCreationActivity) getActivity();

        groupMembers = new ArrayList<>();

    }

    private void createGroupMembers() {

        // 1. 현재 만드는 유저.
//        groupMembers.add(groupCreationActivity.curMoimingUser);

        // 2. 체크된 친구들 불러오기.
        // check 된 친구들을 가져온 다음, 그 친구들을 각각 호출에서 MoimingUser로 만든 후에 List 에 추가해주는 For loop.
        /*for(){

        }
        */

        // end

    }


    private void createCurGroup() {


        // 1. 만들고자 하는 그룹을 생성하여 DB에 저장한다.
        // 현재 bgImg설정 null, groupMemberCnt = 1;
/*
        MoimingGroupRequestDTO groupDTO = new MoimingGroupRequestDTO(groupName, groupInfo, groupCreationActivity.curMoimingUser.getUuid(), "", 1);
        TransferModel<MoimingGroupRequestDTO> transferModel = new TransferModel<>(groupDTO);

        GroupRetrofitService groupRetrofitService = GlobalRetrofit.getInstance().getRetrofit().create(GroupRetrofitService.class);


        // TODO 비동기 방식
        groupRetrofitService.groupCreationRequest(transferModel)
                .subscribeOn(Schedulers.io()) // 새로운 스레드에서 진행된다.
                .observeOn(AndroidSchedulers.mainThread()) // SubscribeOn 은 UI update 하기 위해 대기하는 것
                .subscribe(new Observer<TransferModel<MoimingGroupResponseDTO>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                        //TODO OnSubscribe 역할 인지 필요.

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<MoimingGroupResponseDTO> responseModel) {

                        createdGroup = responseModel.getData().convertToVO();

                        Log.w(GroupCreationActivity.GROUP_CREATION_TAG, createdGroup.toString());
                        // 잘 그룹이 형성되었는지 확인. 안되었으면 onError 전달, 되었으면 onComplete 전달.
                        // 이 그룹의 uuid 가 필요함.
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        Log.e(GroupCreationActivity.GROUP_CREATION_TAG, e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                        // 3. 체크된 친구들(현유저 포함)을 하나하나 그룹과 연결해준다.
                        createUgLinkers();

                    }
                });*/
    }



    private void createUgLinkers() { // TODO 아직 개발 미완성 : 실제 초대 Process 를 구현해야

        // recent_notice / notice_cnt 는 보내줘야 하는 데이터가 아니라 생성되어야 하는 데이터이다.
        // 따라서 굳이 일일이 모든 GroupMemberVO 를 하나씩 보내서 생성할 필요 없음.
        // 그냥 각 VO 의 uuid 를 보내서 이 그룹과 맺어주면 된다.
        // 보내야 하는것 : 현 그룹 uuid / List of uuids

        Map<Integer, UUID> groupMemberUuids = new HashMap<>();

        for (MoimingUserVO members : groupMembers) {

            int i = 0;

            groupMemberUuids.put(i++, members.getUuid());

        }

        UGLinkerRequestDTO ugLinkerDTO = new UGLinkerRequestDTO(createdGroup.getUuid(), groupMemberUuids);
        TransferModel<UGLinkerRequestDTO> requestModel = new TransferModel<>(ugLinkerDTO);

        UGLinkerRetrofitService linkerRetrofitService = GlobalRetrofit.getInstance().getRetrofit().create(UGLinkerRetrofitService.class);
        linkerRetrofitService.ugLinkRequest(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel transferModel) {

                        if (transferModel.getResultCode() != 200) {

                            Toast.makeText(groupCreationActivity.getApplicationContext(), "그룹 생성 but 멤버 연결 실패.. ", Toast.LENGTH_SHORT).show();

                            groupCreationActivity.finish();


                        } else { // 생성이 성공되었다면 데이터 전달 및 GroupActivity 실행

                            Toast.makeText(groupCreationActivity.getApplicationContext(), "그룹 생성 성공!", Toast.LENGTH_SHORT).show();

                            Intent groupIntent = new Intent(groupCreationActivity, GroupActivity.class);

//                            groupIntent.putExtra(getResources().getString(R.string.moiming_user_data_key), groupCreationActivity.curMoimingUser);
                            groupIntent.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) createdGroup);

                            startActivity(groupIntent);

                            groupCreationActivity.finish();

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {
                            Log.e(GroupCreationActivity.GROUP_CREATION_TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

}
