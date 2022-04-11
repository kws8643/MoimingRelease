package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

public class MyPageNotificationSettingActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Switch swAppNotice, swSessionReq, swSessionConf, swGroupInv, swGroupPayment;
    private SharedPreferences spFcm;


    private boolean isAppNotice, isSessionReq, isSessionConf, isGroupInv, isGroupPayment;

    private View.OnClickListener switchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Switch sw = (Switch) v;
            SharedPreferences.Editor fcmSettingEditor = spFcm.edit();

            switch (sw.getId()) {
                case R.id.switch_app_notice:
                    if (sw.isChecked()) { // 체크되는 거면
                        fcmSettingEditor.putBoolean("fcm_app_notice", true);
                    } else {
                        fcmSettingEditor.putBoolean("fcm_app_notice", false);
                    }
                    break;

                case R.id.switch_send_request:
                    if (sw.isChecked()) { // 체크되는 거면
                        fcmSettingEditor.putBoolean("fcm_session_request", true);
                    } else {
                        fcmSettingEditor.putBoolean("fcm_session_request", false);
                    }
                    break;

                case R.id.switch_send_confirm:
                    if (sw.isChecked()) { // 체크되는 거면
                        fcmSettingEditor.putBoolean("fcm_session_confirm", true);
                    } else {
                        fcmSettingEditor.putBoolean("fcm_session_confirm", false);
                    }
                    break;

                case R.id.switch_group_invite:
                    if (sw.isChecked()) { // 체크되는 거면
                        fcmSettingEditor.putBoolean("fcm_group_invite", true);
                    } else {
                        fcmSettingEditor.putBoolean("fcm_group_invite", false);
                    }
                    break;

                case R.id.switch_group_payment:
                    if (sw.isChecked()) { // 체크되는 거면
                        fcmSettingEditor.putBoolean("fcm_group_payment", true);
                    } else {
                        fcmSettingEditor.putBoolean("fcm_group_payment", false);
                    }
                    break;
            }

            fcmSettingEditor.apply();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_notification_setting);

        initView();

        initParams();

        initSwitch();

        swAppNotice.setOnClickListener(switchListener);
        swSessionReq.setOnClickListener(switchListener);
        swSessionConf.setOnClickListener(switchListener);
        swGroupInv.setOnClickListener(switchListener);
        swGroupPayment.setOnClickListener(switchListener);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {

        btnBack = findViewById(R.id.btn_finish_my_page_noti_setting);
        swAppNotice = findViewById(R.id.switch_app_notice);
        swSessionReq = findViewById(R.id.switch_send_request);
        swSessionConf = findViewById(R.id.switch_send_confirm);
        swGroupInv = findViewById(R.id.switch_group_invite);
        swGroupPayment = findViewById(R.id.switch_group_payment);

    }

    private void initParams() {

        spFcm = getSharedPreferences(SplashActivity.NOTI_SP_NAME, MODE_PRIVATE);

    }

    private void initSwitch() { // SP Value 들을 체크하여 상태대로 스위치를 적용한다.

        isAppNotice = spFcm.getBoolean("fcm_app_notice", true);
        isSessionReq = spFcm.getBoolean("fcm_session_request", true);
        isSessionConf = spFcm.getBoolean("fcm_session_confirm", true);
        isGroupInv = spFcm.getBoolean("fcm_group_invite", true);
        isGroupPayment = spFcm.getBoolean("fcm_group_payment", true);

        swAppNotice.setChecked(isAppNotice);
        swSessionReq.setChecked(isSessionReq);
        swSessionConf.setChecked(isSessionConf);
        swGroupInv.setChecked(isGroupInv);
        swGroupPayment.setChecked(isGroupPayment);

    }
}