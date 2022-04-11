package com.example.moimingrelease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moimingrelease.moiming_model.dialog.AppProcessDialog;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingUserResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.auth.AuthRetrofitService;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.annotations.SerializedName;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.Profile;
import com.kakao.sdk.user.model.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    public final String LOGIN_TAG = "LOGIN_TAG";
    public static String SP_TOKEN = "SP_TOKEN";

    private SharedPreferences sharedPreferences;

    private ConstraintLayout btnKakao;
    private FirebaseAuth firebaseAuth;

    private AppProcessDialog processDialog;


    private void initView() {

        btnKakao = findViewById(R.id.btnKakao);

    }

    private void initParams() {

        firebaseAuth = FirebaseAuth.getInstance();
        processDialog = new AppProcessDialog(LoginActivity.this);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        initParams();

        btnKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                processDialog.show();

                kakaoLogin();
            }
        });
    }


    //---------------------------------------------------------------------------------Kakao OAuth


    private TransferModel<Map<String, Object>> responseDataModel;

    private void kakaoLogin() {

        UserApiClient.getInstance().loginWithKakaoAccount(getApplicationContext(), new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken token, Throwable error) { //OAuthToken 객체: 액토, 리토, 각각 만료시간의 정보를 담는 객체.
                if (error != null) {

                    Toast.makeText(getApplicationContext(), "카카오 로그인을 다시 시도하여 주세요", Toast.LENGTH_SHORT).show();
                    processDialog.finish();


                } else {


                    sharedPreferences = getSharedPreferences(SP_TOKEN, MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sharedPreferences.edit();


                    String accessToken = token.getAccessToken();
                    String refreshToken = token.getRefreshToken();


                    spEditor.putString("kakao_access_token", accessToken);
                    spEditor.putString("kakao_refresh_token", refreshToken);


                    Log.w(LOGIN_TAG, accessToken + "\n::\n" + refreshToken);


                    spEditor.commit();


                    // 액토를 전달을 통한 API 서버의 인증 진행
                    AuthRetrofitService authService = GlobalRetrofit.getInstance().getRetrofit().create(AuthRetrofitService.class);

                    authService.loginOrSignupUser2(accessToken)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<TransferModel<Map<String, Object>>>() {
                                @Override
                                public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@io.reactivex.rxjava3.annotations.NonNull TransferModel<Map<String, Object>> responseModel) {

                                    responseDataModel = responseModel;


                                }

                                @Override
                                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                                    Toast.makeText(getApplicationContext(), "카카오를 연동할 수 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                                    if (e.getMessage() != null) {
                                        Log.e(LOGIN_TAG, e.getMessage());
                                    }
                                }

                                @Override
                                public void onComplete() {

                                    Map<String, Object> responseData = responseDataModel.getData();

                                    boolean isOurUser = (boolean) responseData.get("isUser");

                                    if (isOurUser) { // 우리 유저임을 확인

                                        // 1 헤더에 있는 JWT 토큰을 꺼낸 뒤 저장한다.
                                        String jwtToken = responseDataModel.getAuthentication();
                                        Log.w(LOGIN_TAG, jwtToken);

                                        SharedPreferences.Editor spEditor = sharedPreferences.edit();
                                        spEditor.putString("jwt_token", jwtToken);
                                        spEditor.commit();      // 참고) commit - 즉시 동기 저장, apply - 비동기 저장.

                                        // 2 객체에 담겨있는 MoimingUser 데이터들을 통해서 현재 로그인한 객체를 만든다
                                        MoimingUserVO loginUser = MoimingUserVO.parseDataToVO((Map<String, Object>) responseData.get("loginUser"));
                                        Log.w(LOGIN_TAG, loginUser.toString());


                                        //TODO: Login 시 Firebase Auth 도 받은 이메일로 로그인을 진행한다.
                                        loginWithFirebase(loginUser);


                                    } else { // 우리 유저가 아님. 회원가입을 실행

                                        // 1 카카오를 통해 서버단에서 전달해준 데이터들을 회원가입으로 전달해준다
                                        Intent toSignup = new Intent(LoginActivity.this, SignupActivity.class);

                                        String kakaoUid = (String) responseData.get("oauthUid");
                                        String oauthEmail = (String) responseData.get("oauthEmail");
                                        String userPfImg = "";

                                        if (responseData.get("userPfImg") != null) {
                                            userPfImg = (String) responseData.get("userPfImg");
                                        }

                                        // 2 받은 데이터들을 Intent 에 담고 회원가입을 실행한다
                                        toSignup.putExtra("oauth_uid", kakaoUid);
                                        toSignup.putExtra("oauth_email", oauthEmail);
                                        toSignup.putExtra("user_pf_img", userPfImg);

                                        processDialog.finish();

                                        startActivity(toSignup);

                                        finish();
                                    }

                                }
                            });
                }
                return null;
            }
        });
    }


    // TODO: 얘는 꼭 필요한건지???? Splash 에서 자동로그인 하면 진행 안하는 Process, 로그인이 되어 있는 상태로 보는건지?
    private void loginWithFirebase(MoimingUserVO loginUser) { // 받아온 이메일과 oauthUid 로 진행.


        firebaseAuth.signInWithEmailAndPassword(loginUser.getUserEmail(), loginUser.getOauthUid())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // 3 현 로그인 유저를 VO로 넘기고, MainActivity를 실행한다.
                            Intent startMoiming = new Intent(LoginActivity.this, MainActivity.class);
                            startMoiming.putExtra(getResources().getString(R.string.moiming_user_data_key), loginUser);

                            processDialog.finish();
                            startActivity(startMoiming);
                            finish();
                        } else {

                            Toast.makeText(getApplicationContext()
                                    , "2차인증에 실패하였습니다, 잠시후 다시 시도하여 주세요", Toast.LENGTH_SHORT);

                        }
                    }
                });

    }


/*
    private void firebaseAuthentication(String oauth_provider, String accessToken) {


        getFirebaseJwt(oauth_provider, accessToken).continueWithTask(new Continuation<String, Task<AuthResult>>() {
            @Override
            public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {

                String firebaseToken = task.getResult(); // 액토를 통해 반환한 FB Token

                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                Toast.makeText(getApplicationContext(), "성공!", Toast.LENGTH_SHORT);

                return null;
//                return mAuth.signInWithCustomToken(firebaseToken); // Auth. 인증이 되던지 회원가입을 하던지.

            }

        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() { // 객체 존재 (이미 존재 or 생성 성공)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Auth 가지고 생성이 된 상태.
                    *//**
     * TODO 여기서 데이터베이서를 조회하여 객체 상태가 어떤지를 통해 회원가입 / 로그인 을 구분한다.
     *      따라서 여기서부터 MySQL User Table 생성 -> SpringBoot 구축을 통해 로직 구현 시작.
     *      User Table 존재 여부로 결정!
     *
     *//*


                } else { // 생성 실패.

                    Toast.makeText(getApplicationContext(), "객체 반환 실패: Server Error ", Toast.LENGTH_SHORT).show();

                    if (task.getException() != null) {
                        Log.e(LOGIN_TAG, task.getException().toString());

                    }

                }
            }
        });

    }*/

/*

    private Task<String> getFirebaseJwt(String oauth_provider, final String accessToken) {

        final TaskCompletionSource<String> source = new TaskCompletionSource<>();

        String url;

        if (oauth_provider == "KAKAO") {
            url = getResources().getString(R.string.kakao_validation_server_domain);
        } else { // 현재는 이럴 일은 없음.
            url = null;
        }

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", accessToken);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(validationObject), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.w(LOGIN_TAG, response.toString());

                    String firebaseToken = response.getString("firebase_token");

                    source.setResult(firebaseToken);


                } catch (Exception e) {

                    source.setException(e);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(LOGIN_TAG, error.getMessage());

                source.setException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", accessToken);
                return params;
            }
        };

        queue.add(request);

        return source.getTask();
    }
*/



/* // 카카오 연동 키해시 함
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
*/
}