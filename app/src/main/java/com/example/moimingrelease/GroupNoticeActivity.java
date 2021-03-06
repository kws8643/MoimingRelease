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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.moiming_model.extras.GroupNoticeDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupRetrofitService;
import com.example.moimingrelease.network.TransferModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GroupNoticeActivity extends AppCompatActivity {

    private MoimingUserVO curUser;
    private MoimingGroupVO curGroup;
    private List<MoimingMembersDTO> curMemebers;
    private MoimingMembersDTO createdMember;

    private TextView textCreatorName, textCreatedAt;
    private TextView textNoticeInfo;
    private EditText inputNoticeInfo;
    private ImageView imgCreatorPf;
    private ConstraintLayout layoutNoticeInfo;
    private Button btnSetNotice;

    private boolean isNoticeChanging = false;
    private boolean isNoticeChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notice);

        receiveIntent();

        initView();

        initParams();


        btnSetNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changePageStatus();

            }
        });

        inputNoticeInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (inputNoticeInfo.getText().toString().length() != 0) {


                    btnSetNotice.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_main_bg, null));
                    btnSetNotice.setEnabled(true);

                } else { // ???????????????

                    btnSetNotice.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_light_main_bg, null));
                    btnSetNotice.setEnabled(false);

                }

            }
        });

    }

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent.getExtras() != null) {

            // TODO: User, Group, ????????? MemberDto ???????????? ??????!
            curUser = (MoimingUserVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            curGroup = (MoimingGroupVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));
            curMemebers = receivedIntent.getParcelableArrayListExtra(getResources().getString(R.string.group_members_data_key));

        } else {

            finish();

        }

    }

    private void initView() {

        textCreatorName = findViewById(R.id.text_notice_creator_name);
        textCreatedAt = findViewById(R.id.text_created_at);

        imgCreatorPf = findViewById(R.id.img_notice_creator_pf_img);
        imgCreatorPf.setBackground(new ShapeDrawable(new OvalShape()));
        imgCreatorPf.setClipToOutline(true);

        layoutNoticeInfo = findViewById(R.id.layout_notice_info);
        btnSetNotice = findViewById(R.id.btn_set_notice);

        textNoticeInfo = findViewById(R.id.text_notice_info);
        inputNoticeInfo = findViewById(R.id.input_notice_info);
        inputNoticeInfo.setVisibility(View.GONE);

    }

    private void initParams() {

        checkCurNoticeCreator();

    }

    private void checkCurNoticeCreator(){

        if (curGroup.getNoticeCreatorUuid() != null) { // ????????? ?????? ??????.
            for (int i = 0; i < curMemebers.size(); i++) {
                if (curGroup.getNoticeCreatorUuid().toString().equals(curMemebers.get(i).getUuid().toString())) {
                    createdMember = curMemebers.get(i); // ?????????.
                    break;
                }

                if(curGroup.getNoticeCreatorUuid().toString().equals(curUser.getUuid().toString())) { // ?????? ??????

                    createdMember = new MoimingMembersDTO(); // ??? ?????? ???????????? ?????? ???????????? ?????? DTO ??????

                    createdMember.setUserName(curUser.getUserName());
                    createdMember.setUserPfImg(curUser.getUserPfImg());

                }
            }

            // ??????????????? ???????????? ?????? ==> ??? ?????? ????????? ????????? ?????? ??????.
            if (createdMember == null) {
                createdMember = new MoimingMembersDTO();
                createdMember.setUserName("?????? ?????? ?????????");
                // TODO: ?????? ?????? ??????
            }


        } else {

            // TODO: ?????? ?????????

        }

        setNoticeCreatorInfo();
    }

    private void setNoticeCreatorInfo() {

        if (createdMember != null) {

            String userName = createdMember.getUserName();
            String userPfImg = createdMember.getUserPfImg();

            // ?????? ?????????
            LocalDateTime createdTime = curGroup.getNoticeCreatedAt();

            int year = createdTime.getYear();

            int month = createdTime.getMonthValue();
            String strMonth = String.valueOf(month);

            int dom = createdTime.getDayOfMonth();
            String strDom = String.valueOf(dom);

            if (month < 10) {
                strMonth = "0" + strMonth;
            }

            if (dom < 10) {
                strDom = "0" + strDom;
            }

            String dateText = year + "." + strMonth + "." + strDom;


            textCreatorName.setText(userName);
            textCreatedAt.setText(dateText);
            textNoticeInfo.setText(curGroup.getNotice());
            setImgUserPfImg(userPfImg);


        } else {

            // TODO: ?????? ????????? ??? ????????? ???????????? ?????? ??????. (?????? ??? ?????????)


        }
    }

    private void changePageStatus() {

        if (!isNoticeChanging) { // false ?????? ?????? ????????? ???.

            isNoticeChanging = true;

            // ?????? ????????? ??????, ???????????? ????????? ??????, ??????????????? ??????.
            btnSetNotice.setText("??? ???");
            layoutNoticeInfo.setBackgroundResource(R.drawable.shape_round_bg_notice_change);
            textNoticeInfo.setVisibility(View.GONE);
            inputNoticeInfo.setVisibility(View.VISIBLE);


        } else { // ????????? Notice ??? ???????????? ???

            if (inputNoticeInfo.getText().length() > 0) {
                isNoticeChanging = false;

                // TODO: ?????? ?????? ?????????
                updateNoticeOnGroup();


            } else {
                Toast.makeText(getApplicationContext(), "??????????????? ??????????????????!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void setImgUserPfImg(String userPfImg) {

        if (!userPfImg.equals("")) {
            Glide.with(this).load(userPfImg).into(imgCreatorPf);
        }
    }

    private void updateNoticeOnGroup() {


        GroupRetrofitService groupRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupRetrofitService.class);

        GroupNoticeDTO changeNoticeDto = new GroupNoticeDTO(curGroup.getUuid(), curUser.getUuid()
                , inputNoticeInfo.getText().toString());

        groupRetrofit.groupNoticeRequest(new TransferModel<>(changeNoticeDto))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<MoimingGroupResponseDTO>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<MoimingGroupResponseDTO> responseModel) {

                        //  ????????? curGroup ??????????????? GroupActivity ?????? ?????? ??? ?????? ??????, MainRefresh ???????????????.
                        curGroup = responseModel.getData().convertToVO();

                        isNoticeChanged = true;

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            Log.e("Group Notice Error:: ", e.getMessage());

                        }

                    }

                    @Override
                    public void onComplete() {

                        btnSetNotice.setText("+ ??? ?????? ????????????");
                        layoutNoticeInfo.setBackgroundResource(R.drawable.shape_round_bg_notice_view);
                        textNoticeInfo.setVisibility(View.VISIBLE);
                        inputNoticeInfo.setVisibility(View.GONE);
                        textNoticeInfo.setText(curGroup.getNotice()); // ???????????? ????????? ?????????

                        checkCurNoticeCreator();

                        Toast.makeText(getApplicationContext(), "??????????????? ?????????????????????", Toast.LENGTH_SHORT).show();
                        MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = true; // Group ?????? ???????????? Refresh ??????

                    }
                });
    }

    @Override
    public void finish() {

        if(isNoticeChanged){

            Intent noticeData = new Intent();

            noticeData.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);

            setResult(RESULT_OK, noticeData);

        }

        super.finish();
    }
}