package com.example.moimingrelease.fragments_signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moimingrelease.R;
import com.example.moimingrelease.SignupActivity;

public class SignupPolicyFragment extends Fragment {

    SignupActivity signupActivity;

    /**
     * CheckBox 순서대로
     * cbAgreeAll - 전체동의 체크 버튼
     * cbAgree1 - [필수] 이용약관 동의
     * cbAgree2 - [필수] 만 14세 이상 확인
     * cbAgree3 - [필수] 개인정보 수집 및 이용 동의
     * cbAgree4 - [선택] 위치기반 서비스 이용 동의
     * cbAgree5 - [선택] 마케팅 알림 수신 동의
     */

    private CheckBox cbAgreeAll, cbAgree1, cbAgree2, cbAgree3, cbAgree4, cbAgree5;
    private TextView btnViewAgree1, btnViewAgree2, btnViewAgree3, btnViewAgree4, btnViewAgree5;
    private Button btnFinishPolicy;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_signup_policy, container, false);

        initParams();
        initView(view);

        cbAgreeAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {

                    cbAgree1.setChecked(true);
                    cbAgree2.setChecked(true);
                    cbAgree3.setChecked(true);
                    cbAgree4.setChecked(true);
                    cbAgree5.setChecked(true);

                } else {

                    cbAgreeAll.setChecked(true);

                }
            }
        });

        btnFinishPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signupActivity.changeFragment(SignupActivity.FRAGMENT_PASS_INDEX);

            }
        });

        return view;

    }


    private void initParams() {

        signupActivity = (SignupActivity) getActivity();


    }

    private void initView(View view) {

        cbAgreeAll = view.findViewById(R.id.cb_agree_all);

        cbAgree1 = view.findViewById(R.id.cb_agree_1);
        cbAgree2 = view.findViewById(R.id.cb_agree_2);
        cbAgree3 = view.findViewById(R.id.cb_agree_3);
        cbAgree4 = view.findViewById(R.id.cb_agree_4);
        cbAgree5 = view.findViewById(R.id.cb_agree_5);

        btnViewAgree1 = view.findViewById(R.id.btn_view_agree1);
        btnViewAgree2 = view.findViewById(R.id.btn_view_agree2);
        btnViewAgree3 = view.findViewById(R.id.btn_view_agree3);
        btnViewAgree4 = view.findViewById(R.id.btn_view_agree4);
        btnViewAgree5 = view.findViewById(R.id.btn_view_agree5);

        btnFinishPolicy = view.findViewById(R.id.btn_finish_policy);

    }


}
