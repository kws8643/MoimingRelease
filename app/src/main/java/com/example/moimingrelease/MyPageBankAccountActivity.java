package com.example.moimingrelease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

public class MyPageBankAccountActivity extends AppCompatActivity {

    private MoimingUserVO curUser;
    private ImageView btnBack, btnUpdateAccount, viewBankLogo;
    private TextView textBank, textBankNumber, textNoInfo;
    private boolean isInfoChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_bank_account);

        receiveIntent();

        initView();

        initParams();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Register Activity 로 가는데, StartActivityForResult 로 가야함.
                Intent registerIntent = new Intent(MyPageBankAccountActivity.this, RegisterBankAccountActivity.class);

                registerIntent.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);

                startActivityForResult(registerIntent, 100);
            }
        });

        viewUserBankInfo();
    }

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent != null) {
            curUser = (MoimingUserVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        }
    }

    private void initView() {

        btnBack = findViewById(R.id.btn_finish_my_page_bank_account);
        btnUpdateAccount = findViewById(R.id.btn_edit_bank_account);
        viewBankLogo = findViewById(R.id.view_bank_logo);
        textBank = findViewById(R.id.text_user_bank);
        textBankNumber = findViewById(R.id.text_user_bank_account_number);
        textNoInfo = findViewById(R.id.text_no_bank_info);

    }

    private void initParams() {
    }


    private void viewUserBankInfo() {

        if (curUser.getBankName() != null) {

            textNoInfo.setVisibility(View.GONE);
            viewBankLogo.setVisibility(View.VISIBLE);
            textBank.setVisibility(View.VISIBLE);
            textBankNumber.setVisibility(View.VISIBLE);

            String bankName = curUser.getBankName();
            String bankNum = curUser.getBankNumber();

            textBank.setText(bankName);
            textBankNumber.setText(bankNum);

        } else { // 아직 은행 정보가 없음.

            Toast.makeText(getApplicationContext(), "등록된 계좌정보가 없으므로 등록 화면으로 이동합니다", Toast.LENGTH_SHORT).show();
            btnUpdateAccount.performClick();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                curUser = (MoimingUserVO) data.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));

                isInfoChanged = true;

                viewUserBankInfo();

            } else { // 등록을 안하고 돌아왔음.

                Toast.makeText(getApplicationContext(), "원활한 서비스 이용을 위해 계좌 등록이 필요합니다", Toast.LENGTH_SHORT).show();

                finish();

            }
        }
    }

    @Override
    public void finish() {

        /*Intent finishing = new Intent();

        if (isInfoChanged) {
            finishing.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
            setResult(RESULT_OK, finishing);
        } else {
            setResult(RESULT_CANCELED, finishing);
        }*/
        super.finish();
    }
}