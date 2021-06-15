package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.example.moimingrelease.fragments_signup.SignupCheckInfoFragment;
import com.example.moimingrelease.fragments_signup.SignupPassFragment;
import com.example.moimingrelease.fragments_signup.SignupPolicyFragment;

public class SignupActivity extends AppCompatActivity {

    /**
     * SignUp Activity Flow
     * <p>
     * 약관 동의 -> 핸드폰 본인 인증 -> 정보 확인 -> 시작. (화면 만듦)
     *
     * @param savedInstanceState
     */

    public static final int FRAGMENT_POLICY_INDEX = 1;
    public static final int FRAGMENT_PASS_INDEX = 2;
    public static final int FRAGMENT_CHECK_INFO_INDEX = 3;

    FragmentManager fragmentManager;
    private SignupPolicyFragment policyFragment;
    private SignupPassFragment passFragment;
    private SignupCheckInfoFragment checkInfoFragment;

    private String oauthUid;
    private String oauthEmail;
    private String userPfImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initView();

        initParams();

        fragmentManager.beginTransaction().add(R.id.frame_signup, policyFragment).commit();

    }

    private void receiveIntent(){

        if(getIntent() != null){

            oauthUid = getIntent().getExtras().getString("oauth_uid");
            oauthEmail = getIntent().getExtras().getString("oauth_email");
            userPfImg = getIntent().getExtras().getString("user_pf_img");

        }else{

            Toast.makeText(getApplicationContext(), "카카오 정보를 받아오지 못하였습니다. 앱 종료 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            finish();

        }

    }


    private void initView() {


    }

    private void initParams() {

        receiveIntent();

        fragmentManager = getSupportFragmentManager();

        policyFragment = new SignupPolicyFragment();
        passFragment = new SignupPassFragment();
        checkInfoFragment = new SignupCheckInfoFragment();


    }

    public void changeFragment(int index) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);

        if (index == FRAGMENT_POLICY_INDEX) {

            fragmentTransaction.replace(R.id.frame_signup, policyFragment).commit();

        } else if (index == FRAGMENT_PASS_INDEX) {

            fragmentTransaction.replace(R.id.frame_signup, passFragment).commit();

        } else if (index == FRAGMENT_CHECK_INFO_INDEX) { //?

            fragmentTransaction.replace(R.id.frame_signup, checkInfoFragment).commit();

        }

    }

    public String getOauthUid(){

        return this.oauthUid;
    }

    public String getOauthEmail(){

        return this.oauthEmail;
    }

    public String getUserPfImg(){
        return this.userPfImg;
    }
}