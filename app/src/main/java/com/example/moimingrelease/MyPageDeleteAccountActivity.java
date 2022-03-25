package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

public class MyPageDeleteAccountActivity extends AppCompatActivity {

    private MoimingUserVO curUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_delete_account);

        receiveIntent();

        initView();

        initParams();

    }


    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent != null) {
            curUser = (MoimingUserVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        }
    }

    private void initView() {

    }

    private void initParams() {
    }

    // TODO: 마지막 체크 박스 동의 여부에 따라서 버튼 색상 변경해 주기, 위에 필수 사항 체크박스들도 고려
/*

    private void checkButtonChangeStatus() {

        // 4개가 다 Empty 면 못함
        if (stateChangedUserUuid.isEmpty() && stateChangedNmuUuid.isEmpty() && stateChangedSentNmuUuid.isEmpty() && stateChangedSentUserUuid.isEmpty()) {

            btnSessionStatusChange.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_light_main_bg, null));
            btnSessionStatusChange.setEnabled(false);
        } else {

            btnSessionStatusChange.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_main_bg, null));
            btnSessionStatusChange.setEnabled(true);
        }
    }
*/

}