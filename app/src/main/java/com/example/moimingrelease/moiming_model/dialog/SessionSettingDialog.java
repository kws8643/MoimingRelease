package com.example.moimingrelease.moiming_model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.SessionDialogCallBack;


public class SessionSettingDialog extends Dialog {

    private Context mContext;
    private LinearLayout btnDelete;
    private SessionDialogCallBack settingCallback;

    public SessionSettingDialog(@NonNull Context mContext, SessionDialogCallBack settingCallback) {
        super(mContext);
        this.mContext = mContext;
        this.settingCallback = settingCallback;

        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_session_setting);

        btnDelete = findViewById(R.id.btn_delete_session);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                settingCallback.selectDelete();

                finish();

            }
        });

    }

    private void finish() {

        this.dismiss();
    }
}
