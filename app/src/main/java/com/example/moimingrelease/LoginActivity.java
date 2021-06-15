package com.example.moimingrelease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
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
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.auth.AuthRetrofitService;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.Profile;
import com.kakao.sdk.user.model.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    public final String LOGIN_TAG = "LOGIN_TAG";

    private ConstraintLayout btnKakao;
    private Button btnToSignup;

    private void initView() {

        btnKakao = findViewById(R.id.btnKakao);
        btnToSignup = findViewById(R.id.btn_to_signup);

    }

    private void initParams() {


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
                kakaoLogin();
            }
        });


        btnToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, SignupActivity.class));

            }
        });

    }


    //---------------------------------------------------------------------------------Kakao OAuth


    private void kakaoLogin() {

        UserApiClient.getInstance().loginWithKakaoAccount(getApplicationContext(), new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken token, Throwable error) { //OAuthToken 객체: 액토, 리토, 각각 만료시간의 정보를 담는 객체.
                if (error != null) {

                    Log.e(LOGIN_TAG, "카카오 로그인 실패:: ", error);

                } else {

                    String accessToken = token.getAccessToken();
                    String refreshToken = token.getRefreshToken();

                    Log.w(LOGIN_TAG, "카카오 계정 A토큰:: " + accessToken);
                    Log.w(LOGIN_TAG, "카카오 계정 R토큰:: " + refreshToken);

                    /**
                     1. ACCESS_TOKEN 으로 로그인을 요청한다. (우리 서버에)
                     2. 카카오 측으로 A_T 를 전달하여(REST API 통신) 유저 인증을 진행 받는다.
                     3. 반환되는 유저 정보에 한하여
                     **/


                    AuthRetrofitService authService = GlobalRetrofit.getInstance().getRetrofit().create(AuthRetrofitService.class);

                    authService.confirmKakaoToken(accessToken).enqueue(new Callback<TransferModel<Map<String, Object>>>() {
                        @Override
                        public void onResponse(Call<TransferModel<Map<String, Object>>> call, Response<TransferModel<Map<String, Object>>> response) {

                            Log.w(LOGIN_TAG, response.body().toString());

                            if (response.isSuccessful()) {

                                TransferModel<Map<String, Object>> authResponse = response.body();

                                Map<String, Object> responseData = authResponse.getData();

                                boolean isVerified = (boolean) responseData.get("isVerified");

                                if (isVerified) { // 우리 유저입니다. 토큰을 받아왔을 것입니다.

                                    Toast.makeText(getApplicationContext(), "유저 존재", Toast.LENGTH_SHORT).show();


                                } else { // 우리 유저가 아닙니다. 회원가입 process 를 진행합니다.

                                    Intent toSignup = new Intent(LoginActivity.this, SignupActivity.class);

                                    String kakaoUid = (String) responseData.get("oauth_uid");
                                    String oauthEmail = (String) responseData.get("oauth_email");
                                    String userPfImg = "";

                                    if (responseData.get("user_pf_img") != null) {
                                        userPfImg = (String) responseData.get("user_pf_img");
                                    }

                                    toSignup.putExtra("oauth_uid", kakaoUid);
                                    toSignup.putExtra("oauth_email", oauthEmail);
                                    toSignup.putExtra("user_pf_img", userPfImg);

                                    startActivity(toSignup);

                                    finish();

                                }


                            } else {

                            }

                        }

                        @Override
                        public void onFailure(Call<TransferModel<Map<String, Object>>> call, Throwable t) {

                            Toast.makeText(getApplicationContext(), "카카오를 연동할 수 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                            Log.e(LOGIN_TAG, t.getMessage());

                        }
                    });


                }
                return null;
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