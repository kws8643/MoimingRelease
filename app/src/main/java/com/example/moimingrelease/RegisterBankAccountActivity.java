package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingUserRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingUserResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.UserRetrofitService;

import java.io.Serializable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterBankAccountActivity extends AppCompatActivity {

    /**
     * String bankName 은행별 코드 이름
     * KB = 카카오뱅크
     * NH = 농협은행
     * IBK = 기업은행
     * HN = 하나은행
     * WR= 우리은행
     * KM = 국민은행
     * SC = SC제일은행
     * DK = 대구은행
     * BS = 부산은행
     * KJ = 광주은행
     * SM = 새마을 금고
     * KN = 경남은행
     * JB = 전북은행
     * JJ = 제주은행
     * SU = 산업은행
     * SH = 신한은행
     * SHU = 신협은행
     * SUH = 수협은행
     * CT = 시티은행
     * K = 케이뱅크
     * TS = 토스뱅크
     * WC = 우체국
     */

    private MoimingUserVO curUser;

    private Button btnUpdateAccount;

    private ConstraintLayout btnSelectBank;
    private TextView textSelectedBank;
    private EditText inputBankNumber;
    private ImageView btnBack;

    private String selectedBankName;
    private String bankNumber;

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent.getExtras() != null) {

            curUser = (MoimingUserVO) receivedIntent.getSerializableExtra(getResources().getString(R.string.moiming_user_data_key));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_bank_account);

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

                bankNumber = inputBankNumber.getText().toString();
                if (bankNumber == null || selectedBankName == null || selectedBankName.isEmpty() || bankNumber.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "정보를 모두 기입하여 주세요", Toast.LENGTH_SHORT).show();
                } else if (bankNumber.length() < 5) {
                    Toast.makeText(getApplicationContext(), "계좌를 바르게 기입하여 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    updateAccountInfo();
                }
            }
        });

        btnSelectBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // TODO: 선택용 만들기 필요

                textSelectedBank.setText("카카오뱅크");
                selectedBankName = "KB";

            }
        });

    }

    private void initView() {

        btnBack = findViewById(R.id.btn_back_bank_account);

        btnUpdateAccount = findViewById(R.id.btn_register_bank_account);
        textSelectedBank = findViewById(R.id.text_selected_bank);
        btnSelectBank = findViewById(R.id.btn_select_bank);

        inputBankNumber = findViewById(R.id.input_bank_account);

    }

    private void initParams() {

    }

    private void updateAccountInfo() { // 서버랑 통신하는 곳

        UserRetrofitService userRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(UserRetrofitService.class);

        MoimingUserRequestDTO updateUser = new MoimingUserRequestDTO(curUser.getUuid(), curUser.getOauthUid()
                , curUser.getOauthType(), curUser.getUserName(), curUser.getUserEmail()
                , selectedBankName, bankNumber
                , curUser.getUserPfImg(), curUser.getPhoneNumber());

        TransferModel<MoimingUserRequestDTO> requestModel = new TransferModel<>(updateUser);

        userRetrofit.userUpdate(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<MoimingUserResponseDTO>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<MoimingUserResponseDTO> responseModel) {

                        curUser = responseModel.getData().convertToVO(); // 유저 업데이트.

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {
                            Log.w("ERROR", e.getMessage());
                            e.printStackTrace();
                        }

                        Intent finishedRegister = new Intent();

                        setResult(Activity.RESULT_CANCELED, finishedRegister);

                        finish();

                    }

                    @Override
                    public void onComplete() {

                        isEdited = true;

                        Intent finishedRegister = new Intent();

                        MainActivity.IS_USER_UPDATED = true;

                        finishedRegister.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);

                        setResult(Activity.RESULT_OK, finishedRegister);

                        finish();
                    }
                });
    }

    private boolean isEdited = false;

    @Override
    public void finish() {

        if (!isEdited) {
            setResult(RESULT_CANCELED);
        }
        super.finish();
    }

}