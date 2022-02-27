package com.example.moimingrelease.fragments_signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moimingrelease.R;
import com.example.moimingrelease.SignupActivity;

import java.util.HashMap;
import java.util.Map;

public class SignupPolicyFragment extends Fragment {

    SignupActivity signupActivity;

    /**
     * CheckBox 순서대로
     * cbAgreeAll - 전체동의 체크 버튼
     * cbAgree1 - [필수] 이용약관 동의
     * cbAgree2 - [필수] 만 14세 이상 확인
     * cbAgree3 - [필수] 개인정보 수집 및 이용 동의
     * cbAgree4 - [선택] 마케팅 알림 수신 동의
     */

    private CheckBox cbAgreeAll, cbAgree1, cbAgree2, cbAgree3, cbAgree4;
    private TextView btnViewAgree1, btnViewAgree2, btnViewAgree3, btnViewAgree4;
    private Button btnFinishPolicy;

    private Map<Integer, Boolean> agreeMap;


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
                    agreeMap.put(0, true);
                    agreeMap.put(1, true);
                    agreeMap.put(2, true);
                    agreeMap.put(3, true);

                } else {

                    cbAgree1.setChecked(false);
                    cbAgree2.setChecked(false);
                    cbAgree3.setChecked(false);
                    cbAgree4.setChecked(false);
                    agreeMap.put(0, false);
                    agreeMap.put(1, false);
                    agreeMap.put(2, false);
                    agreeMap.put(3, false);

                }
            }
        });

        cbAgree1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) agreeMap.put(0, true); // 선택시
                else agreeMap.put(0, false);
            }
        });

        cbAgree2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) agreeMap.put(1, true); // 선택시
                else agreeMap.put(1, false);
            }
        });

        cbAgree3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) agreeMap.put(2, true); // 선택시
                else agreeMap.put(2, false);
            }
        });

        cbAgree4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) agreeMap.put(3, true); // 선택시
                else agreeMap.put(3, false);
            }
        });

        btnFinishPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 필수 항목 체크 확인
                if (agreeMap.get(0) && agreeMap.get(1) && agreeMap.get(2)) {
                    signupActivity.changeFragment(SignupActivity.FRAGMENT_PASS_INDEX);
                } else {
                    Toast.makeText(signupActivity.getApplicationContext(), "필수 동의 항목에 대해서 동의 후 진행할 수 있습니다", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;

    }


    private void initParams() {

        signupActivity = (SignupActivity) getActivity();

        agreeMap = new HashMap<>();
        agreeMap.put(0, false);
        agreeMap.put(1, false);
        agreeMap.put(2, false);
        agreeMap.put(3, false);


    }

    private void initView(View view) {

        cbAgreeAll = view.findViewById(R.id.cb_agree_all);

        cbAgree1 = view.findViewById(R.id.cb_agree_1);
        cbAgree2 = view.findViewById(R.id.cb_agree_2);
        cbAgree3 = view.findViewById(R.id.cb_agree_3);
        cbAgree4 = view.findViewById(R.id.cb_agree_4);

        btnViewAgree1 = view.findViewById(R.id.btn_view_agree1);
        btnViewAgree2 = view.findViewById(R.id.btn_view_agree2);
        btnViewAgree3 = view.findViewById(R.id.btn_view_agree3);
        btnViewAgree4 = view.findViewById(R.id.btn_view_agree4);

        btnFinishPolicy = view.findViewById(R.id.btn_finish_policy);

    }

    public Map<Integer, Boolean> getAgreeMap() {

        return this.agreeMap;

    }

}
