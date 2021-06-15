package com.example.moimingrelease.fragments_signup;

import android.content.Intent;
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
import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.moiming_model.request_dto.MoimingUserRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingUserResponseDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.UserRetrofitService;
import com.example.moimingrelease.R;
import com.example.moimingrelease.SignupActivity;
import com.google.android.material.textfield.TextInputEditText;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_signup_check_info, container, false);

        initParams();

        initView(view);

        btnFinishConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signupUser();

            }
        });

        return view;

    }

    private void initParams() {

        signupActivity = (SignupActivity) getActivity();


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

        // TODO: RETROFIT 함수에 대한 CLASS 화가 중요할 듯. 매번 이렇게 다 소환시키기는 코드가 더러움.
        //       JAVA를 잘 사용해보자.

        // RETROFIT CONNECTION

        // 1. Request Object 만들기.
        MoimingUserRequestDTO userRequestDTO = new MoimingUserRequestDTO(signupActivity.getOauthUid(), "KAKAO", userName, userEmail, signupActivity.getUserPfImg(), phoneNumber);
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

                    Toast.makeText(signupActivity.getApplicationContext(), "통신에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                    MoimingUserVO savedUser = responseModel.getData().convertToVO();

                    Intent startMoiming = new Intent(signupActivity, MainActivity.class);
                    startMoiming.putExtra("moiming_user", savedUser);


                    signupActivity.startActivity(startMoiming);
                    signupActivity.finish();


                } else {

                    Toast.makeText(signupActivity.getApplicationContext(), "정보 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<TransferModel<MoimingUserResponseDTO>> call, Throwable t) {

                Toast.makeText(signupActivity.getApplicationContext(), "통신에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                if (t.getMessage() != null) Log.e("TAG", t.getMessage());
            }
        });


    }

}
