package com.example.moimingrelease.moiming_model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.example.moimingrelease.GroupPaymentActivity;
import com.example.moimingrelease.GroupPaymentDateSelectActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.RegisterAccountListener;
import com.example.moimingrelease.moiming_model.moiming_vo.GroupPaymentVO;
import com.example.moimingrelease.moiming_model.request_dto.GroupPaymentRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.GroupPaymentResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupPaymentRetrofitService;
import com.example.moimingrelease.network.TransferModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterAccountDialog extends Dialog {

    private Context context;

    private TextView btnCancel, btnConfirm;

    private RegisterAccountListener btnListener;

    public RegisterAccountDialog(@NonNull Context context, RegisterAccountListener btnListener) {

        super(context);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.context = context;
        this.btnListener = btnListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_register_bank_account);

        initView();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnListener.cancelOrConfirm(true);

                finish();

            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnListener.cancelOrConfirm(false);

                finish();
            }
        });

    }

    private void initView() {

        btnCancel = findViewById(R.id.btn_account_create_cancel);
        btnConfirm = findViewById(R.id.btn_create_account);

    }

    private void finish() {

        this.dismiss();

    }

    @Override
    public void onBackPressed() {
        btnCancel.performClick();
    }
}
