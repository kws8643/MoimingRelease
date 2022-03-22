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

                } else { // 빈칸일경우

                    btnSetNotice.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_light_main_bg, null));
                    btnSetNotice.setEnabled(false);

                }

            }
        });

    }

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent.getExtras() != null) {

            // TODO: User, Group, 등록한 MemberDto 가져오면 될듯!
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

        if (curGroup.getNoticeCreatorUuid() != null) { // 등록은 되어 있음.
            for (int i = 0; i < curMemebers.size(); i++) {
                if (curGroup.getNoticeCreatorUuid().toString().equals(curMemebers.get(i).getUuid().toString())) {
                    createdMember = curMemebers.get(i); // 발견함.
                    break;
                }

                if(curGroup.getNoticeCreatorUuid().toString().equals(curUser.getUuid().toString())) { // 나인 경우

                    createdMember = new MoimingMembersDTO(); // 나 임을 표시하기 위한 일시적인 멤버 DTO 생성

                    createdMember.setUserName(curUser.getUserName());
                    createdMember.setUserPfImg(curUser.getUserPfImg());

                }
            }

            // 끝나고까지 못찾았을 경우 ==> 더 이상 그룹의 멤버가 아닌 경우.
            if (createdMember == null) {
                createdMember = new MoimingMembersDTO();
                createdMember.setUserName("그룹 탈퇴 사용자");
                // TODO: 기본 사진 깔기
            }


        } else {

            // TODO: 최초 입력임

        }

        setNoticeCreatorInfo();
    }

    private void setNoticeCreatorInfo() {

        if (createdMember != null) {

            String userName = createdMember.getUserName();
            String userPfImg = createdMember.getUserPfImg();

            // 날짜 채우기
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

            // TODO: 최초 입력일 떄 어떻게 해야할지 구현 필요. (일단 걍 냅둔다)


        }
    }

    private void changePageStatus() {

        if (!isNoticeChanging) { // false 였던 경우 바꿔야 함.

            isNoticeChanging = true;

            // 버튼 텍스트 변경, 레이아웃 테두리 변경, 에딧텍스트 변경.
            btnSetNotice.setText("완 료");
            layoutNoticeInfo.setBackgroundResource(R.drawable.shape_round_bg_notice_change);
            textNoticeInfo.setVisibility(View.GONE);
            inputNoticeInfo.setVisibility(View.VISIBLE);


        } else { // 변경된 Notice 를 저장하는 곳

            if (inputNoticeInfo.getText().length() > 0) {
                isNoticeChanging = false;

                // TODO: 변경 통신 성공시
                updateNoticeOnGroup();


            } else {
                Toast.makeText(getApplicationContext(), "공지사항을 입력해주세요!", Toast.LENGTH_SHORT).show();
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

                        //  이걸로 curGroup 설정해줘서 GroupActivity 에서 받을 수 있게 하고, MainRefresh 넣어놔둔다.
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

                        btnSetNotice.setText("+ 새 공지 등록하기");
                        layoutNoticeInfo.setBackgroundResource(R.drawable.shape_round_bg_notice_view);
                        textNoticeInfo.setVisibility(View.VISIBLE);
                        inputNoticeInfo.setVisibility(View.GONE);
                        textNoticeInfo.setText(curGroup.getNotice()); // 공지사항 내용을 넣는다

                        checkCurNoticeCreator();

                        Toast.makeText(getApplicationContext(), "공지사항이 변경되었습니다", Toast.LENGTH_SHORT).show();
                        MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = true; // Group 정보 수정으로 Refresh 필요

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