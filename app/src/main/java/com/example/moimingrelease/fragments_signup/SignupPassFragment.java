package com.example.moimingrelease.fragments_signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moimingrelease.R;
import com.example.moimingrelease.SignupActivity;

public class SignupPassFragment extends Fragment {


    SignupActivity signupActivity;

    private Button btnFinishPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_signup_pass_auth, container, false);

        initParams();
        initView(view);

        btnFinishPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signupActivity.changeFragment(SignupActivity.FRAGMENT_CHECK_INFO_INDEX);

            }
        });

        return view;

    }

    private void initParams() {

        signupActivity = (SignupActivity) getActivity();


    }

    private void initView(View view) {

        btnFinishPass = view.findViewById(R.id.btn_finish_pass_auth);

    }

}
