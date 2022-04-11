package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moimingrelease.app_listener_interface.CustomDialogCallBack;
import com.example.moimingrelease.moiming_model.dialog.CustomConfirmDialog;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

import java.util.HashMap;
import java.util.Map;

public class MyPageDeleteAccountActivity extends AppCompatActivity {

    private MoimingUserVO curUser;

    private Map<Integer, Boolean> cbMap;

    private CheckBox cbResign, cbHard, cbSystem, cbOther, cbNo;

    private CheckBox cbDeleteAgree;

    private Button btnDelete;

    private ImageView btnBack;

    private CustomDialogCallBack callback = new CustomDialogCallBack() {
        @Override
        public void onConfirm() {

            processDeleteAccount();
        }
    };

    private View.OnClickListener cbListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            CheckBox cb = (CheckBox) v;

            switch (cb.getId()) {

                case R.id.cb_resign:
                    cbMap.put(0, cb.isChecked());
                    break;
                case R.id.cb_hard:
                    cbMap.put(1, cb.isChecked());
                    break;
                case R.id.cb_system_fuck:
                    cbMap.put(2, cb.isChecked());
                    break;
                case R.id.cb_other_service:
                    cbMap.put(3, cb.isChecked());
                    break;
                case R.id.cb_no_use:
                    cbMap.put(4, cb.isChecked());
                    break;
                case R.id.cb_delete_agree:
                    changeButtonStatus(cb.isChecked());
                    break;
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_delete_account);

        receiveIntent();

        initView();

        initParams();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "휴..", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cbMap.get(0) && !cbMap.get(1) && !cbMap.get(2) && !cbMap.get(3) && !cbMap.get(4)) { // 하나라도 동의한게 없음

                    Toast.makeText(getApplicationContext(), "필수동의 사항을 최소한 한 개 선택해 주세요", Toast.LENGTH_SHORT).show();

                } else {

                    CustomConfirmDialog deleteAccountDialog = new CustomConfirmDialog(MyPageDeleteAccountActivity.this, callback
                            , "모이밍을 이용해주셔서 감사합니다", "더욱 발전된 모습으로 다시 만나겠습니다.");

                    deleteAccountDialog.show();
                }
            }
        });

    }

    private void processDeleteAccount() {

        // 1. 서버타고 들어가서 계정 삭제 (계정 삭제 후 처리는 어떻게 할지 생각 쭉 해봐야 함)

        // 2. 성공시 탈퇴 사유 FB 에 보내 놓는다. (FB 계정도 삭제 필요)
        /** FB Data
         * Collection: DeleteAccount
         * Document: MoimingUser Uuid
         * Field: 1. String reason = "1.2.3", "0.1.2" 등의 형태로 저장
                  2. String feedBack = "유저가 적은 FeedBack";

         */
        // 3. 동기 작업으로 jwt token 있으면 삭제 후 LoginActivity 로 이동한다.

    }

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent != null) {
            curUser = (MoimingUserVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        }
    }

    private void initView() {

        btnBack = findViewById(R.id.btn_finish_delete_account);

        cbResign = findViewById(R.id.cb_resign);
        cbHard = findViewById(R.id.cb_hard);
        cbSystem = findViewById(R.id.cb_system_fuck);
        cbOther = findViewById(R.id.cb_other_service);
        cbNo = findViewById(R.id.cb_no_use);
        cbDeleteAgree = findViewById(R.id.cb_delete_agree);

        cbResign.setOnClickListener(cbListener);
        cbHard.setOnClickListener(cbListener);
        cbSystem.setOnClickListener(cbListener);
        cbOther.setOnClickListener(cbListener);
        cbNo.setOnClickListener(cbListener);
        cbDeleteAgree.setOnClickListener(cbListener);


        btnDelete = findViewById(R.id.btn_delete_account);
    }

    private void initParams() {

        cbMap = new HashMap<>(); // def set
        cbMap.put(0, false);
        cbMap.put(1, false);
        cbMap.put(2, false);
        cbMap.put(3, false);
        cbMap.put(4, false);


    }

    private void changeButtonStatus(boolean isChecked) {
        // 마지막꺼 체크 여부에 따라서 버튼 색깔은 바뀐다.
        // 누를 때 cb 도 다 최소 한개 체크되었는지 판단해준다.

        if (isChecked) { // 동의한다.
            btnDelete.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_main_bg, null));
            btnDelete.setEnabled(true);
        } else { // 해제한다.
            btnDelete.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_light_main_bg, null));
            btnDelete.setEnabled(false);
        }

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