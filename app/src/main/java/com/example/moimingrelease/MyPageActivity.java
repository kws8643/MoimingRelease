package com.example.moimingrelease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.app_listener_interface.CustomDialogCallBack;
import com.example.moimingrelease.moiming_model.dialog.CustomConfirmDialog;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

public class MyPageActivity extends AppCompatActivity {

    private MoimingUserVO curUser;

    private TextView btnToUserInfo, textMyName, textLogout;
    private ImageView imgMyPf, btnBack;

    private ConstraintLayout btnAlarmSetting, btnService, btnBankAccount;

    private CustomDialogCallBack logOutCallback = new CustomDialogCallBack() {
        @Override
        public void onConfirm() {
            processLogout();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        receiveIntent();

        initView();

        initParams();

        btnToUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 수정 가능하므로 onActivityResult 로 연결
                Intent toUserInfo = new Intent(MyPageActivity.this, MyPageUserInfoActivity.class);

                toUserInfo.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);

                startActivity(toUserInfo);

            }
        });

        btnAlarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toAlarmSetting = new Intent(MyPageActivity.this, MyPageNotificationSettingActivity.class);

                startActivity(toAlarmSetting);
            }
        });

        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toServiceInfo = new Intent(MyPageActivity.this, MyPageServiceInfoActivity.class);
                startActivity(toServiceInfo);
            }
        });

        btnBankAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toBankInfo = new Intent(MyPageActivity.this, MyPageBankAccountActivity.class);

                toBankInfo.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);

                startActivity(toBankInfo);
            }
        });

        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomConfirmDialog logOutDialog = new CustomConfirmDialog(MyPageActivity.this, logOutCallback
                        , "현재 계정으로부터\n로그아웃하시겠습니까?", "");

                logOutDialog.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent != null) {
            curUser = (MoimingUserVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        }
    }

    private void initView() {

        btnBack = findViewById(R.id.btn_finish_my_page);

        // 유저 정보 설정
        btnToUserInfo = findViewById(R.id.btn_to_user_info);
        imgMyPf = findViewById(R.id.img_my_pf);
        imgMyPf.setBackground(new ShapeDrawable(new OvalShape()));
        imgMyPf.setClipToOutline(true);
        assert curUser.getUserPfImg() != null;
        setImgUserPfImg(curUser.getUserPfImg());

        textMyName = findViewById(R.id.text_my_name);
        textMyName.setText(curUser.getUserName());

        btnAlarmSetting = findViewById(R.id.layout_alarm_setting);
        btnBankAccount = findViewById(R.id.layout_account_check);
        btnService = findViewById(R.id.layout_service);

        textLogout = findViewById(R.id.text_logout);
    }

    private void initParams() {
    }

    public void setImgUserPfImg(String userPfImg) {

        if (!userPfImg.equals("")) {
            Glide.with(this).load(userPfImg).into(imgMyPf);
        }
    }


    //  TODO: 이게 맞는지?
    private void processLogout() {

        // 로그아웃 진행 // 저장된 토큰을 삭제하고 밖으로 꺼낸다.
        SharedPreferences jwtSp = getSharedPreferences(LoginActivity.SP_TOKEN, MODE_PRIVATE);

        SharedPreferences.Editor edit = jwtSp.edit();
        edit.putString("jwt_token", ""); //jwt Token 을 지운다.
        edit.apply();

        // static 변수 초기화
        MainActivity.IS_USER_UPDATED = false;
        MainActivity.IS_NOTIFICATION_REFRESH_NEEDED = false;
        MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = false;
        GroupActivity.SESSION_LIST_REFRESH_FLAG = false;
        GroupActivity.GROUP_INFO_REFRESH_FLAG = false;

        Intent logout = new Intent(MyPageActivity.this, LoginActivity.class);
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 쌓인 액티비티들을 모두 없앤다.

        startActivity(logout);

        Toast.makeText(getApplicationContext(), "안전하게 로그아웃 되었습니다", Toast.LENGTH_SHORT).show();

        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // TODO: 같은 일을 하는 것 같기도?
            if (requestCode == 100) { // UserInfo Changed

                curUser = (MoimingUserVO) data.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));

            } else if (requestCode == 200) { // UserBankChanged
                curUser = (MoimingUserVO) data.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            }
        } else {

        }
    }
}