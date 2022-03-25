package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

public class MyPageActivity extends AppCompatActivity {

    private MoimingUserVO curUser;

    private TextView btnToUserInfo, textMyName;
    private ImageView imgMyPf;

    private ConstraintLayout btnAlarmSetting, btnService;

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
        });{

        }

    }

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent != null) {
            curUser = (MoimingUserVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        }
    }

    private void initView() {

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

        btnService = findViewById(R.id.layout_service);
    }

    private void initParams() {
    }

    public void setImgUserPfImg(String userPfImg) {

        if (!userPfImg.equals("")) {
            Glide.with(this).load(userPfImg).into(imgMyPf);
        }
    }
}