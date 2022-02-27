package com.example.moimingrelease.moiming_model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.moimingrelease.GroupActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionCreationActivity;
import com.example.moimingrelease.app_listener_interface.SessionDialogCallBack;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SessionDeleteDialog extends Dialog {

    private Context mContext;
    private String curSessionName;
    private SessionDialogCallBack dialogCallback;
    private TextView btnDelete, btnCancel;
    private TextView textTitle;


    public SessionDeleteDialog(Context mContext, SessionDialogCallBack dialogCallback, String curSessionName) {

        super(mContext);
        this.mContext = mContext;
        this.dialogCallback = dialogCallback;
        this.curSessionName = curSessionName;

        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_session_delete);

        textTitle = findViewById(R.id.text_session_delete_title);
        btnDelete = findViewById(R.id.btn_session_delete_confirm);
        btnCancel = findViewById(R.id.btn_session_delete_cancel);

        String textTitleInfo = "[" + curSessionName + "]을 삭제하시겠습니까?";
        textTitle.setText(textTitleInfo);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogCallback.deleteSession();

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

    private void finish() {

        this.dismiss();
    }

}
