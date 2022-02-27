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
import com.example.moimingrelease.app_listener_interface.GroupExitDialogListener;

public class GroupExitInGroupDialog extends Dialog {

    private LinearLayout btnExitGroup;

    private GroupExitDialogListener exitDialogListener;

    private String groupUuid;

    public GroupExitInGroupDialog(@NonNull Context context, GroupExitDialogListener exitDialogListener, String groupUuid) {

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
        setContentView(R.layout.view_dialog_group_exit_in_group);

        initView();


        btnExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialogListener.initExitConfirmDialog(false, groupUuid); // 시도는 False
            }
        });


    }


    private void initView() {

        btnExitGroup = findViewById(R.id.btn_exit_group_in_group);
    }


    private void finish() {

        this.dismiss();

    }


}
