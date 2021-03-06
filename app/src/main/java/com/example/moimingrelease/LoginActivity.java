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
            public Unit invoke(OAuthToken token, Throwable error) { //OAuthToken ??????: ??????, ??????, ?????? ??????????????? ????????? ?????? ??????.
                if (error != null) {

                    Toast.makeText(getApplicationContext(), "????????? ???????????? ?????? ???????????? ?????????", Toast.LENGTH_SHORT).show();
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


                    // ????????? ????????? ?????? API ????????? ?????? ??????
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

                                    Toast.makeText(getApplicationContext(), "???????????? ????????? ??? ????????????. ?????? ??????????????????.", Toast.LENGTH_SHORT).show();

                                    if (e.getMessage() != null) {
                                        Log.e(LOGIN_TAG, e.getMessage());
                                    }
                                }

                                @Override
                                public void onComplete() {

                                    Map<String, Object> responseData = responseDataModel.getData();

                                    boolean isOurUser = (boolean) responseData.get("isUser");

                                    if (isOurUser) { // ?????? ???????????? ??????

                                        // 1 ????????? ?????? JWT ????????? ?????? ??? ????????????.
                                        String jwtToken = responseDataModel.getAuthentication();
                                        Log.w(LOGIN_TAG, jwtToken);

                                        SharedPreferences.Editor spEditor = sharedPreferences.edit();
                                        spEditor.putString("jwt_token", jwtToken);
                                        spEditor.commit();      // ??????) commit - ?????? ?????? ??????, apply - ????????? ??????.

                                        // 2 ????????? ???????????? MoimingUser ??????????????? ????????? ?????? ???????????? ????????? ?????????
                                        MoimingUserVO loginUser = MoimingUserVO.parseDataToVO((Map<String, Object>) responseData.get("loginUser"));
                                        Log.w(LOGIN_TAG, loginUser.toString());


                                        //TODO: Login ??? Firebase Auth ??? ?????? ???????????? ???????????? ????????????.
                                        loginWithFirebase(loginUser);


                                    } else { // ?????? ????????? ??????. ??????????????? ??????

                                        // 1 ???????????? ?????? ??????????????? ???????????? ??????????????? ?????????????????? ???????????????
                                        Intent toSignup = new Intent(LoginActivity.this, SignupActivity.class);

                                        String kakaoUid = (String) responseData.get("oauthUid");
                                        String oauthEmail = (String) responseData.get("oauthEmail");
                                        String userPfImg = "";

                                        if (responseData.get("userPfImg") != null) {
                                            userPfImg = (String) responseData.get("userPfImg");
                                        }

                                        // 2 ?????? ??????????????? Intent ??? ?????? ??????????????? ????????????
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


    // TODO: ?????? ??? ??????????????????? Splash ?????? ??????????????? ?????? ?????? ????????? Process, ???????????? ?????? ?????? ????????? ?????????????
    private void loginWithFirebase(MoimingUserVO loginUser) { // ????????? ???????????? oauthUid ??? ??????.


        firebaseAuth.signInWithEmailAndPassword(loginUser.getUserEmail(), loginUser.getOauthUid())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // 3 ??? ????????? ????????? VO??? ?????????, MainActivity??? ????????????.
                            Intent startMoiming = new Intent(LoginActivity.this, MainActivity.class);
                            startMoiming.putExtra(getResources().getString(R.string.moiming_user_data_key), loginUser);

                            processDialog.finish();
                            startActivity(startMoiming);
                            finish();
                        } else {

                            Toast.makeText(getApplicationContext()
                                    , "2???????????? ?????????????????????, ????????? ?????? ???????????? ?????????", Toast.LENGTH_SHORT);

                        }
                    }
                });

    }


/*
    private void firebaseAuthentication(String oauth_provider, String accessToken) {


        getFirebaseJwt(oauth_provider, accessToken).continueWithTask(new Continuation<String, Task<AuthResult>>() {
            @Override
            public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {

                String firebaseToken = task.getResult(); // ????????? ?????? ????????? FB Token

                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                Toast.makeText(getApplicationContext(), "??????!", Toast.LENGTH_SHORT);

                return null;
//                return mAuth.signInWithCustomToken(firebaseToken); // Auth. ????????? ????????? ??????????????? ?????????.

            }

        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() { // ?????? ?????? (?????? ?????? or ?????? ??????)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Auth ????????? ????????? ??? ??????.
                    *//**
     * TODO ????????? ????????????????????? ???????????? ?????? ????????? ???????????? ?????? ???????????? / ????????? ??? ????????????.
     *      ????????? ??????????????? MySQL User Table ?????? -> SpringBoot ????????? ?????? ?????? ?????? ??????.
     *      User Table ?????? ????????? ??????!
     *
     *//*


                } else { // ?????? ??????.

                    Toast.makeText(getApplicationContext(), "?????? ?????? ??????: Server Error ", Toast.LENGTH_SHORT).show();

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
        } else { // ????????? ?????? ?????? ??????.
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



/* // ????????? ?????? ????????? ???
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