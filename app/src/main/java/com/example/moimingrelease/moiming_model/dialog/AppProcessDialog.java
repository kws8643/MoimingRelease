package com.example.moimingrelease.moiming_model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.moimingrelease.R;

public class AppProcessDialog extends Dialog {


    public AppProcessDialog(@NonNull Context context) {
        super(context);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_app_process);

    }

    @Override
    public void show() {
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        super.show();
    }

    public void finish() {
        this.dismiss();
    }
}
