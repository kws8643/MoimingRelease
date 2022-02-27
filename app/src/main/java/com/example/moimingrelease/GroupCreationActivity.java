package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moimingrelease.fragments_group_creation.GroupCreationInfoFragment;
import com.example.moimingrelease.fragments_group_creation.GroupCreationMembersFragment;
import com.example.moimingrelease.fragments_main.MoimingHomeFragment;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingGroupRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupRetrofitService;
import com.example.moimingrelease.network.TransferModel;

import java.io.Serializable;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GroupCreationActivity extends AppCompatActivity {

    public static final int FRAGMENT_INFO_INDEX = 0;
    public static final int FRAGMENT_MEMBERS_INDEX = 1;
    public static final String GROUP_CREATION_TAG = "GROUP_CREATION_TAG";

    private MoimingUserVO curUser;
    private MoimingGroupVO createdGroup;

/*
    FragmentManager fragmentManager;
    private GroupCreationInfoFragment infoFragment;
    private GroupCreationMembersFragment membersFragment;*/

    private Button btnCreateGroup;
    private EditText inputGroupName, inputGroupInfo;
    private ImageView btnSelectGroupPfImg;


    private void receiveIntent() {

        Intent receivedData = getIntent();

        if (receivedData.getExtras() != null) {

            curUser = (MoimingUserVO) receivedData.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);

        receiveIntent();

        initView();

        initParams();

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputGroupName.getText().length() > 0) {
                    createCurGroup();
                } else {
                    Toast.makeText(v.getContext(), "그룹명은 필수 항목입니다", Toast.LENGTH_SHORT).show();
                }
            }
        });


//        fragmentManager.beginTransaction().add(R.id.frame_group_creation, infoFragment).commit();


    }

    private void initView() {

        btnCreateGroup = findViewById(R.id.btn_create_group);

        inputGroupName = findViewById(R.id.input_group_name);
        inputGroupInfo = findViewById(R.id.input_group_info);
        btnSelectGroupPfImg = findViewById(R.id.btn_select_group_pf_img);
        btnSelectGroupPfImg.setClipToOutline(true);


    }


    private void initParams() {


    /*    fragmentManager = getSupportFragmentManager();
        infoFragment = new GroupCreationInfoFragment();
        membersFragment= new GroupCreationMembersFragment();*/

    }

    private void createCurGroup() {

        String groupName = inputGroupName.getText().toString();
        String groupInfo = "";

        if (inputGroupInfo.getText().length() > 0) { // NULL 방지
            groupInfo = inputGroupInfo.getText().toString();
        }

        // 1. 만들고자 하는 그룹을 생성하여 DB에 저장한다.
        // 현재 bgImg설정 null, groupMemberCnt = 1;
        MoimingGroupRequestDTO groupDTO = new MoimingGroupRequestDTO(groupName, groupInfo, curUser.getUuid(), "", 1);
        TransferModel<MoimingGroupRequestDTO> transferModel = new TransferModel<>(groupDTO);

        GroupRetrofitService groupRetrofitService = GlobalRetrofit.getInstance().getRetrofit().create(GroupRetrofitService.class);

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

                        // 1. 그룹 생성 요청을 통해 그룹을 보내고, Creator과 Group 을 연결시켜준다.
                        // 2. 생성된 그룹을 반환한다

                        createdGroup = responseModel.getData().convertToVO();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {
                            Log.e(GROUP_CREATION_TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                        Intent groupIntent = new Intent(GroupCreationActivity.this, GroupActivity.class);

                        groupIntent.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);

                        MoimingGroupAndMembersDTO groupAndMembersDTO = new MoimingGroupAndMembersDTO(createdGroup, new ArrayList<MoimingMembersDTO>());
                        groupIntent.putExtra(getResources().getString(R.string.moiming_group_and_members_data_key), groupAndMembersDTO);

                        startActivity(groupIntent);

                        MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = true;

                        finish();

                    }
                });
    }

    /*public void changeFragment(int index, Bundle fragData){

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);

        if(index == FRAGMENT_INFO_INDEX){

            transaction.replace(R.id.frame_group_creation, infoFragment).commit();

        }else if(index == FRAGMENT_MEMBERS_INDEX){

            membersFragment.setArguments(fragData);
            transaction.replace(R.id.frame_group_creation, membersFragment).commit();

        }

    }
*/

}