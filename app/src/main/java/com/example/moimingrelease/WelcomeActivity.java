package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.request_dto.NotificationRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingSessionResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.NotificationRetrofitService;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.fcm.FCMRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class WelcomeActivity extends AppCompatActivity {

    private MoimingUserVO curUser;
    private boolean isFirstUser;

    private Button btnStartMoiming;

    private FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        receiveIntent();

        initView();

        initParams();

        btnStartMoiming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFirstUser) {
                    createWelcomeNotification();
                } else {
                    finish();
                }
            }
        });

    }

    private void receiveIntent() {

        Intent receivedData = getIntent();

        if (receivedData.getExtras() != null) {

            isFirstUser = receivedData.getBooleanExtra("first_user", false);
            if (isFirstUser) {
                curUser = (MoimingUserVO) receivedData.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            }
        } else {

            finish();
        }
    }

    private void initView() {

        btnStartMoiming = findViewById(R.id.btn_start_moiming);

    }

    private void initParams() {

    }

    private void createWelcomeNotification() {

        NotificationRequestDTO welcomeNoti = new NotificationRequestDTO(curUser.getUuid(), null
                , "system", null, null, 0
                , curUser.getUserName() + "님, 모이밍에 오신 것을 환영합니다! 모이밍에 대해서 설명해 드릴게요");

        TransferModel<NotificationRequestDTO> requestModel = new TransferModel<>(welcomeNoti);


        NotificationRetrofitService notiRetro = GlobalRetrofit.getInstance().getRetrofit().create(NotificationRetrofitService.class);
        notiRetro.createSystemNotification(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<String> stringTransferModel) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            Log.e("Notification save error:: ", e.getMessage());

                        }
                    }

                    @Override
                    public void onComplete() {

                        Log.e("Notification save :: ", "Successful!");

                        receiveFcmToken();

                    }
                });

    }

    private void receiveFcmToken() {

        // 현재 접근 중인 로그인 유저의 FCM Token 을 가져온다
        DocumentReference keyDocs = firebaseDB.collection("UserInfo")
                .document(curUser.getUuid().toString());

        keyDocs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull @NotNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    DocumentSnapshot snapshot = task.getResult();
//                    String userUuid = snapshot.getId();

                    if (snapshot.exists()) {
                        Map<String, Object> responseSnap = snapshot.getData();
                        String memberFcmToken = (String) responseSnap.get("fcm_token");

                        try {
                            sendWelcomeFcm(memberFcmToken);
                        } catch (JSONException e) {

                            if (e.getMessage() != null) {
                                Log.e("FCM Error:: ", e.getMessage());
                            }
                        }
                        startMoimingActivity();

                    } else {
                        Log.e("FCM_TAG", "해당 유저의 토큰 정보가 없습니다");
                        Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("FCM_TAG", "해당 문서를 불러오지 못했습니다");
                    Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void sendWelcomeFcm(String fcmToken) throws JSONException {

        String textNoti = curUser.getUserName() + "님, 모이밍에 오신 것을 환영합니다! 모이밍에 대해서 설명해 드릴게요";

        JSONObject jsonSend = FCMRequest.getInstance().buildFcmJsonData("group", String.valueOf(0), ""
                , textNoti, "", "", "", fcmToken);

        RequestBody reBody = RequestBody.create(MediaType.parse("application/json, charset-utf8")
                , String.valueOf(jsonSend));

        FCMRequest.getInstance().sendFcmRequest(getApplicationContext(), reBody);


    }

    private void startMoimingActivity() {

        Intent startMoiming = new Intent(WelcomeActivity.this, MainActivity.class);

        startMoiming.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
        startActivity(startMoiming);

        finish();
    }


}