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
import com.example.moimingrelease.app_listener_interface.GroupExitDialogListener;

public class GroupExitConfirmDialog extends Dialog {

    private TextView btnCancel, btnGroupExit;

    private GroupExitDialogListener exitDialogListener;

    private String groupUuid;

    public GroupExitConfirmDialog(@NonNull Context context, GroupExitDialogListener exitDialogListener, String groupUuid) {

        super(context);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.exitDialogListener = exitDialogListener;
        this.groupUuid = groupUuid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_group_exit);

        initView();

        btnGroupExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exitDialogListener.initExitConfirmDialog(true, groupUuid);

                finish();
            }
        });

    }


    private void initView() {

        btnCancel = findViewById(R.id.btn_exit_cancel);
        btnGroupExit = findViewById(R.id.btn_group_exit_confirm);

    }


    private void finish() {

        this.dismiss();

    }


}
