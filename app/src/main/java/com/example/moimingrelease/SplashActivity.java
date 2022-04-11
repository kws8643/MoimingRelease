package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupRetrofitService;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.auth.AuthRetrofitService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    private final String SPLASH_TAG = "SPLASH_TAG";
    public final static String NOTI_SP_NAME = "FCM_SETTING_SP";
    private boolean isFcmClicked = false;

    private String movingGroupUuid;
    private String movingSessionUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        receiveIntent();

        checkNotificationSetting();

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 200);
    }

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent.getExtras() != null) {

            isFcmClicked = receivedIntent.getBooleanExtra("is_fcm_clicked", false);
            movingGroupUuid = receivedIntent.getExtras().getString(getResources().getString(R.string.main_move_to_group_key), "");
            movingSessionUuid = receivedIntent.getExtras().getString(getResources().getString(R.string.group_move_to_session_key), "");

        }
    }

    private void checkNotificationSetting(){

        SharedPreferences sharedPreferences = getSharedPreferences(NOTI_SP_NAME, MODE_PRIVATE);

        boolean isNotiSet = sharedPreferences.getBoolean("is_notification_set", false);

        if(!isNotiSet){

            SharedPreferences.Editor spEdit = sharedPreferences.edit();
            //1. app notice
            spEdit.putBoolean("fcm_app_notice", true);
            //2. send request
            spEdit.putBoolean("fcm_session_request", true);
            //3. send confirm
            spEdit.putBoolean("fcm_session_confirm", true);
            //4. group invite
            spEdit.putBoolean("fcm_group_invite", true);
            //5. group payment
            spEdit.putBoolean("fcm_group_payment", true);

            spEdit.putBoolean("is_notification_set", true);

            spEdit.apply();

        }

    }


    private void requestAutoLogin() {

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SP_TOKEN, MODE_PRIVATE);

        String savedJwtToken = sharedPreferences.getString("jwt_token", "");

//        String savedJwtToken = "";

        if (savedJwtToken.equals("")) { // 토큰이 없음.

            Log.w(SPLASH_TAG, "토큰이 없음");
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));

            finish();
            return;
        }

        AuthRetrofitService authRetrofitService = GlobalRetrofit.getInstance().getRetrofit().create(AuthRetrofitService.class);

        authRetrofitService.autoLoginByToken(savedJwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<Map<String, Object>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<Map<String, Object>> responseModel) {


                        Map<String, Object> responseData = responseModel.getData();
                        boolean isTokenValidate = (boolean) responseData.get("isValidate");
                        boolean isUserPresent = (boolean) responseData.get("isPresent");

                        if (isTokenValidate && isUserPresent) { // 토큰 유효. 정보들 가져옴.

                            Log.e("Working Log 1: " , "WORKS!");

                            MoimingUserVO loginUser = MoimingUserVO.parseDataToVO((Map<String, Object>) responseData.get("loginUser"));

                            // 3 현 로그인 유저를 VO로 넘기고, MainActivity를 실행한다.
                            Intent startMoiming = new Intent(SplashActivity.this, MainActivity.class);
                            startMoiming.putExtra("is_fcm_clicked", isFcmClicked);
                            startMoiming.putExtra(getResources().getString(R.string.moiming_user_data_key), loginUser);
                            startMoiming.putExtra(getResources().getString(R.string.main_move_to_group_key), movingGroupUuid);
                            startMoiming.putExtra(getResources().getString(R.string.group_move_to_session_key), movingSessionUuid);

                            startActivity(startMoiming);
                            finish();

                        } else { // 토큰 유효 안함. 로그인 액티비티로 유도.

                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                            finish();
                        }
                    }


                    @Override
                    public void onError(@NonNull Throwable e) {

                        Toast.makeText(getApplicationContext(), "서버 점검중입니다. 나중에 다시 실행해 주세요.", Toast.LENGTH_SHORT).show();

                        if (e.getMessage() != null) {
                            Log.e(SPLASH_TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {


                    }
                });
    }


    class SplashHandler implements Runnable {

        @Override
        public void run() {
            requestAutoLogin();
        }
    }
}