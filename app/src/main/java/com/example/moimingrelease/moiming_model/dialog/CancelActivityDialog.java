package com.example.moimingrelease.moiming_model.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.CancelActivityCallBack;
import com.example.moimingrelease.app_listener_interface.GroupExitDialogListener;

public class CancelActivityDialog extends Dialog {

    private Context context;
    private TextView btnCancel, btnConfirm;

    private TextView textInfoView;
    private String infoText;

    private CancelActivityCallBack finishCallback;


    public CancelActivityDialog(@NonNull Context context, CancelActivityCallBack finishCallback, String infoText) {

        super(context);
        this.context = context;
        this.finishCallback = finishCallback;
        this.infoText = infoText;

        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_cancel_activity);

        initView();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finishCallback.finishActivity();

                finish();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }


    private void initView() {

        btnCancel = findViewById(R.id.btn_cancel_cancel);
        btnConfirm = findViewById(R.id.btn_confirm_cancel);

        textInfoView = findViewById(R.id.text_cancel_dialog_2);
        textInfoView.setText(infoText);

    }


    private void finish() {

        this.dismiss();

    }


}
