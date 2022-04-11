package com.example.moimingrelease.moiming_model.dialog;

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
import com.example.moimingrelease.app_listener_interface.CustomDialogCallBack;

public class CustomConfirmDialog extends Dialog {

    private Context context;
    private TextView btnCancel, btnConfirm;
    private TextView textTitle, textText;

    private String infoTitle;
    private String infoText;

    private CustomDialogCallBack callback;


    public CustomConfirmDialog(@NonNull Context context, CustomDialogCallBack callback, String infoTitle, String infoText) {

        super(context);
        this.context = context;
        this.callback = callback;
        this.infoTitle = infoTitle;
        this.infoText = infoText;

        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_custom_confirm);

        initView();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onConfirm();

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

        btnCancel = findViewById(R.id.btn_custom_cancel);
        btnConfirm = findViewById(R.id.btn_custom_confirm);

        textTitle = findViewById(R.id.text_custom_title);
        textText = findViewById(R.id.text_custom_text);
        textTitle.setText(infoTitle);
        if (!infoText.equals("")) {
            textText.setText(infoText);
        } else {
            textText.setVisibility(View.GONE);
        }


    }


    private void finish() {

        this.dismiss();

    }


}
