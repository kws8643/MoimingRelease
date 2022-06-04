package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moimingrelease.app_adapter.AppExtraMethods;
import com.example.moimingrelease.app_listener_interface.CustomDialogCallBack;
import com.example.moimingrelease.moiming_model.dialog.CustomConfirmDialog;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingGroupEditInfoDto;
import com.example.moimingrelease.moiming_model.request_dto.MoimingGroupRequestDTO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingSessionRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupRetrofitService;
import com.example.moimingrelease.network.TransferModel;

import java.io.Serializable;
import java.security.acl.Group;
import java.time.LocalDateTime;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GroupInfoActivity extends AppCompatActivity {

    private MoimingGroupVO curGroup;
    private int sessionCnt;

    private ImageView imgGroupPf, btnFinish;
    private String editImgPath;

    // 그룹 수정 UI
    private boolean isEditing = false;
    private ImageView btnEdit, btnEditImg;
    private ConstraintLayout layoutEdit;
    private Button btnEditConfirm;
    private EditText inputGroupName, inputGroupDesc;

    // 그룹 정보 view
    private ConstraintLayout layoutView;
    private TextView textGroupName, textGroupDesc;
    private TextView textCreatedDate, textMemberCnt, textPaymentStatus, textSessionCnt;


    private TextWatcher btnStatusWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkBtnStatus();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void receiveIntent() {

        Intent receivedInfo = getIntent();

        if (receivedInfo.getExtras() != null) {
            curGroup = receivedInfo.getExtras().getParcelable(getResources().getString(R.string.moiming_group_data_key));
            sessionCnt = receivedInfo.getExtras().getInt("group_session_cnt", 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        receiveIntent();

        initView();

        initParams();

        setViewData();

        btnEditConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGroupInfo();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEditing) {

                    CustomConfirmDialog deleteAccountDialog = new CustomConfirmDialog(GroupInfoActivity.this
                            , new CustomDialogCallBack() {
                        @Override
                        public void onConfirm() {
                            finish();
                        }
                    }
                            , "이전 페이지로 돌아가시겠습니까?", "입력하신 정보는 삭제됩니다");

                    deleteAccountDialog.show();


                } else {
                    finish();
                }

            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing) {
                    isEditing = true;
                    changeEditorView(isEditing);
                } else {
                    isEditing = false;
                }
            }
        });

    }

    private void initView() {

        imgGroupPf = findViewById(R.id.img_group_info_main_pf);
        imgGroupPf.setClipToOutline(true);

        btnFinish = findViewById(R.id.btn_finish_group_info);

        layoutView = findViewById(R.id.info_edit_false);

        textGroupName = findViewById(R.id.text_group_name_info);
        textGroupDesc = findViewById(R.id.text_group_desc_info);

        textCreatedDate = findViewById(R.id.text_group_created_date);
        textMemberCnt = findViewById(R.id.text_group_member_num);
        textPaymentStatus = findViewById(R.id.text_group_payment_cost);
        textSessionCnt = findViewById(R.id.text_session_cnt);

        // Editing 관련 UI
        btnEdit = findViewById(R.id.btn_group_info_edit);
        btnEditImg = findViewById(R.id.btn_change_group_pf_img);
        layoutEdit = findViewById(R.id.info_edit_true);
        btnEditConfirm = findViewById(R.id.btn_edit_group_info);

        inputGroupName = findViewById(R.id.input_edit_group_name);
        inputGroupName.setHint(curGroup.getGroupName());
        inputGroupName.addTextChangedListener(btnStatusWatcher);

        inputGroupDesc = findViewById(R.id.input_edit_group_desc);
        inputGroupDesc.addTextChangedListener(btnStatusWatcher);
        inputGroupDesc.setHint(curGroup.getGroupInfo());

    }

    private void initParams() {

        editImgPath = "";

    }

    private void setViewData() {

        //set 값
        // 그룹 이름, 그룹 설명
        textGroupName.setText(curGroup.getGroupName());
        if (curGroup.getGroupInfo() != null) {
            if (curGroup.getGroupInfo().length() != 0) {
                textGroupDesc.setText(curGroup.getGroupInfo());
            } else {
                textGroupDesc.setText("그룹을 한줄로 소개해주세요");
            }
        } else {
            textGroupDesc.setText("그룹을 한줄로 소개해주세요");
        }


        // TODO 1. 그룹 프사


        // 2. 생성일자
        LocalDateTime createdAt = curGroup.getCreatedAt();
        int year = createdAt.getYear();
        int month = createdAt.getMonthValue();
        int day = createdAt.getDayOfMonth();
        String strCreatedAt = year + "년 " + month + "월 " + day + "일";
        textCreatedDate.setText(strCreatedAt);

        // 3. 모임원 수
        String strMemberCnt = curGroup.getGroupMemberCnt() + "명";
        textMemberCnt.setText(strMemberCnt);

        // 4. 정산활동 수
        String strSessionCnt = sessionCnt + "개";
        textSessionCnt.setText(strSessionCnt);

        String strPaymentStatus = AppExtraMethods.moneyToWonWon(curGroup.getGroupPayment()) + "원";
        // 5. 회계현황
        if (curGroup.getGroupPayment() < 0) {
            textPaymentStatus.setTextColor(ResourcesCompat.getColor(getResources(), R.color.moimingRed, null));
        }
        textPaymentStatus.setText(strPaymentStatus);

    }

    private void checkBtnStatus() {

        // 4개가 다 Empty 면 못함
        if (inputGroupName.getText().toString().length() == 0 && inputGroupDesc.getText().toString().length() == 0) { // 비활성화 상태
            btnEditConfirm.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_light_main_bg, null));
            btnEditConfirm.setEnabled(false);
        } else { // 활성화 상태
            btnEditConfirm.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_main_bg, null));
            btnEditConfirm.setEnabled(true);
        }
    }

    private MoimingGroupVO editedGroup;

    private void changeGroupInfo() { //  밖에서 정보를 Refresh 한다??

        String editName = "";
        String editDesc = "";

        if (inputGroupName.getText().toString().length() != 0) {
            editName = inputGroupName.getText().toString();
        }

        if (inputGroupDesc.getText().toString().length() != 0) {
            editDesc = inputGroupDesc.getText().toString();
        }

        MoimingGroupEditInfoDto editDto = new MoimingGroupEditInfoDto(curGroup.getUuid(), editName, editDesc, editImgPath);
        TransferModel<MoimingGroupEditInfoDto> requestModel = new TransferModel<>(editDto);

        GroupRetrofitService groupRetro = GlobalRetrofit.getInstance().getRetrofit().create(GroupRetrofitService.class);
        groupRetro.editGroupInfo(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<MoimingGroupResponseDTO>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<MoimingGroupResponseDTO> responseModel) {

                        editedGroup = responseModel.getData().convertToVO();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {
                            Log.e("Group Info Edit ERROR:: ", e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                        MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = true;

                        // 끝나는 정보 전달
                        Intent infoData = new Intent();

                        infoData.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) editedGroup);

                        setResult(RESULT_OK, infoData);

                        finish();
                    }
                });
    }


    private void changeEditorView(boolean isEditing) {

        if (isEditing) {

            btnEdit.setVisibility(View.GONE);
            btnEditImg.setVisibility(View.VISIBLE);

            layoutEdit.setVisibility(View.VISIBLE);
            layoutView.setVisibility(View.GONE);
            btnEditConfirm.setVisibility(View.VISIBLE);

        } else {

            btnEdit.setVisibility(View.VISIBLE);
            btnEditImg.setVisibility(View.GONE);

            layoutEdit.setVisibility(View.GONE);
            layoutView.setVisibility(View.VISIBLE);
            btnEditConfirm.setVisibility(View.GONE);

        }

    }


    @Override
    public void onBackPressed() {

        btnFinish.performClick();
    }


}