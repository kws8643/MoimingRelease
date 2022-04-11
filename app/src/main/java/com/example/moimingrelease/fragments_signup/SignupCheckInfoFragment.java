package com.example.moimingrelease.fragments_signup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.LoginActivity;
import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.WelcomeActivity;
import com.example.moimingrelease.moiming_model.dialog.AppProcessDialog;
import com.example.moimingrelease.moiming_model.extras.UserAndPolicyAgreeDTO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingUserRequestDTO;
import com.example.moimingrelease.moiming_model.request_dto.PolicyAgreeRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingUserResponseDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.PolicyRetrofitService;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.UserRetrofitService;
import com.example.moimingrelease.R;
import com.example.moimingrelease.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupCheckInfoFragment extends Fragment {

    SignupActivity signupActivity;

    private ImageView imgProfile, btnChangePfImg;

    private TextInputEditText textName, textPhoneNumber, textEmail;

    private Button btnFinishConfirm;

    private FirebaseAuth firebaseAuth;

    private Map<Integer, Boolean> userPolicyMap = new HashMap<>();

    private AppProcessDialog processDialog;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle receivedBundle = getArguments();

            int mapSize = receivedBundle.getInt("map_size", 4);

            for (int i = 0; i < mapSize; i++) {

                userPolicyMap.put(i, receivedBundle.getBoolean(String.valueOf(i)));

            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_signup_check_info, container, false);

        initParams();

        initView(view);

        btnFinishConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                processDialog.show();
                signupUser();

            }
        });

        return view;

    }

    private void initParams() {

        signupActivity = (SignupActivity) getActivity();

        processDialog = new AppProcessDialog(signupActivity);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void initView(View view) {

        // 원형 프로필 설정
        imgProfile = view.findViewById(R.id.img_profile);
        imgProfile.setBackground(new ShapeDrawable(new OvalShape()));
        imgProfile.setClipToOutline(true);
        if (!signupActivity.getUserPfImg().equals("")) {
            setPfImg();
        }

        btnChangePfImg = view.findViewById(R.id.btn_change_pf_img);

        textName = view.findViewById(R.id.text_name);
        textPhoneNumber = view.findViewById(R.id.text_phone);

        textEmail = view.findViewById(R.id.text_email); //받아온 이메일 고정.
        textEmail.setText(signupActivity.getOauthEmail());
        textEmail.setEnabled(false);


        btnFinishConfirm = view.findViewById(R.id.btn_finish_confirm);

    }

    private void setPfImg() {
        // Lib 사용
        Glide.with(this).load(signupActivity.getUserPfImg()).into(imgProfile);

    }

    private void createUserPolicyAgreement(UUID curUserUuid) {

        List<PolicyAgreeRequestDTO> userAgreeList = new ArrayList<>();

        //1. Policy 묶음을 만든다.
        PolicyAgreeRequestDTO policy0 = new PolicyAgreeRequestDTO(0, "Usage", userPolicyMap.get(0));
        PolicyAgreeRequestDTO policy1 = new PolicyAgreeRequestDTO(1, "Age", userPolicyMap.get(1));
        PolicyAgreeRequestDTO policy2 = new PolicyAgreeRequestDTO(2, "Privacy", userPolicyMap.get(2));
        PolicyAgreeRequestDTO policy3 = new PolicyAgreeRequestDTO(3, "Marketing", userPolicyMap.get(3));

        userAgreeList.add(policy0);
        userAgreeList.add(policy1);
        userAgreeList.add(policy2);
        userAgreeList.add(policy3);

        UserAndPolicyAgreeDTO requestInfo = new UserAndPolicyAgreeDTO(curUserUuid, userAgreeList);
        TransferModel<UserAndPolicyAgreeDTO> requestModel = new TransferModel<>(requestInfo);

        // TODO: Policy 연결 통신
        PolicyRetrofitService policyRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(PolicyRetrofitService.class);
        policyRetrofit.createAgreedPolicy(requestModel)
                .enqueue(new Callback<TransferModel<String>>() {
                    @Override
                    public void onResponse(Call<TransferModel<String>> call, Response<TransferModel<String>> response) {

                        Toast.makeText(signupActivity.getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<TransferModel<String>> call, Throwable t) {


                        Toast.makeText(signupActivity.getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();


                        if (t.getMessage() != null) {
                            Log.e(SignupActivity.SIGNUP_TAG, t.getMessage());
                        }
                    }
                });

    }

    private void signupUser() {

        String userName;
        String phoneNumber;
        String userEmail;

        if ((textName.getText() != null) && (textPhoneNumber.getText() != null) && (textEmail.getText() != null)) {

            userName = textName.getText().toString();
            phoneNumber = textPhoneNumber.getText().toString();
            userEmail = textEmail.getText().toString();

        } else {

            Toast.makeText(signupActivity.getApplicationContext(), "일부 정보를 불러오지 못했습니다. 재시도 해주세요.", Toast.LENGTH_SHORT).show();

            return;

        }


        // RETROFIT CONNECTION

        // 1. Request Object 만들기.
        MoimingUserRequestDTO userRequestDTO = new MoimingUserRequestDTO(null, signupActivity.getOauthUid(), "KAKAO", userName
                , userEmail, ""
                , "", signupActivity.getUserPfImg(), phoneNumber);

        TransferModel<MoimingUserRequestDTO> transferModel = new TransferModel<>(userRequestDTO);


        // 2. Retrofit 만들기.
        /*Retrofit signupRequest = new Retrofit.Builder()
                .baseUrl(UserRetrofitService.GATEWAY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
*/
        UserRetrofitService userRetrofitService = GlobalRetrofit.getInstance().getRetrofit().create(UserRetrofitService.class);

        Call<TransferModel<MoimingUserResponseDTO>> call = userRetrofitService.userSignupRequest(transferModel);
        call.enqueue(new Callback<TransferModel<MoimingUserResponseDTO>>() {
            @Override
            public void onResponse(Call<TransferModel<MoimingUserResponseDTO>> call, Response<TransferModel<MoimingUserResponseDTO>> response) {

                TransferModel<MoimingUserResponseDTO> responseModel = response.body();

                if (response.isSuccessful()) {

                    String newUserJwtToken = responseModel.getAuthentication();
                    MoimingUserVO savedUser = responseModel.getData().convertToVO();

                    // 1 신규 유저로서 받은 토큰을 저장한다.
                    SharedPreferences sharedPreferences = signupActivity.getSharedPreferences(LoginActivity.SP_TOKEN, Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sharedPreferences.edit();
                    spEditor.putString("jwt_token", newUserJwtToken);
                    spEditor.commit();


                    // TODO: Firebase Auth 등록을 진행한다.
                    firebaseAuth.createUserWithEmailAndPassword(savedUser.getUserEmail(), savedUser.getOauthUid())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        getFirebaseAccessToken(savedUser);

//                                        // 2 신규 유저 데이터를 전달한다.
//                                        Intent startMoiming = new Intent(signupActivity, MainActivity.class);
//                                        startMoiming.putExtra("moiming_user", savedUser);
//
//                                        signupActivity.startActivity(startMoiming);
//                                        signupActivity.finish();
                                    } else {

                                        Toast.makeText(signupActivity.getApplicationContext()
                                                , "Firebase 정보 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                        signupActivity.finish();

                                    }

                                }
                            });

                    // TODO 약관 동의 항목 전송
                    createUserPolicyAgreement(savedUser.getUuid());


                } else {

                    Toast.makeText(signupActivity.getApplicationContext()
                            , "정보 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<TransferModel<MoimingUserResponseDTO>> call, Throwable t) {

                Toast.makeText(signupActivity.getApplicationContext(), "통신에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                if (t.getMessage() != null) Log.e("TAG", t.getMessage());

                signupActivity.finish();
            }
        });
    }

    private void registerFirebaseStore(String fcmToken, MoimingUserVO savedUser) {

        FirebaseFirestore fbDB = FirebaseFirestore.getInstance();

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("fcm_token", fcmToken);

        fbDB.collection("UserInfo")
                .document(savedUser.getUuid().toString())
                .set(dataMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        // 신규 유저에 대해 Welcome Activity 를 실행한다
                        Intent welcome = new Intent(signupActivity, WelcomeActivity.class);
                        welcome.putExtra(getResources().getString(R.string.moiming_user_data_key), savedUser);
                        welcome.putExtra("first_user", true);

                        processDialog.finish();

                        signupActivity.startActivity(welcome);
                        signupActivity.finish();


                        Log.d(SignupActivity.SIGNUP_TAG, "All Registered Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                        Log.d(SignupActivity.SIGNUP_TAG, "Failed Saving: " + e.getMessage().toString());

                    }
                });

    }

    private void getFirebaseAccessToken(MoimingUserVO savedUser) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<String> task) {

                        if (task.isSuccessful()) {
                            registerFirebaseStore(task.getResult(), savedUser);

                        } else {
                            Log.w(SignupActivity.SIGNUP_TAG, task.getException().toString());
                            Toast.makeText(signupActivity.getApplicationContext()
                                    , "FB 토큰 발급을 실패했습니다.", Toast.LENGTH_SHORT).show();
                            signupActivity.finish();
                        }
                    }
                });
    }
}
