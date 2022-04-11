package com.example.moimingrelease.moiming_model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.moimingrelease.GroupActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionCreationActivity;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateSessionDialog extends Dialog {

    private Context mContext;

    private LinearLayout btnSessionFunding, btnSessionDutchpay;

    private MoimingUserVO curUser;

    private MoimingGroupVO curGroup;

    private List<MoimingMembersDTO> curGroupMembers;


    public CreateSessionDialog(Context mContext) {

        super(mContext);
        this.mContext = mContext;
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_session_creation);

        btnSessionDutchpay = findViewById(R.id.btn_session_dutchpay);
        btnSessionFunding = findViewById(R.id.btn_session_funding);

        btnSessionDutchpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent createDutchpay = new Intent(mContext, SessionCreationActivity.class);

                createDutchpay.putExtra(mContext.getResources().getString(R.string.session_creation_type_key), 1);
                createDutchpay.putExtra(mContext.getResources().getString(R.string.moiming_user_data_key), curUser);
                createDutchpay.putExtra(mContext.getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                createDutchpay.putParcelableArrayListExtra(GroupActivity.MOIMING_GROUP_MEMBERS_KEY, (ArrayList<MoimingMembersDTO>) curGroupMembers);
//                createDutchpay.putExtra(mContext.getResources().getString(R.string.session_creation_from_group_activity_flag), true);
                createDutchpay.putExtra(mContext.getResources().getString(R.string.session_creation_with_new_group_flag), false);


                mContext.startActivity(createDutchpay);

                finish();
            }
        });


        btnSessionFunding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent createFunding = new Intent(mContext, SessionCreationActivity.class);

                createFunding.putExtra(mContext.getResources().getString(R.string.session_creation_type_key), 0);
                createFunding.putExtra(mContext.getResources().getString(R.string.moiming_user_data_key), curUser);
                createFunding.putExtra(mContext.getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                createFunding.putParcelableArrayListExtra(GroupActivity.MOIMING_GROUP_MEMBERS_KEY, (ArrayList<MoimingMembersDTO>) curGroupMembers);
//                createFunding.putExtra(mContext.getResources().getString(R.string.session_creation_from_group_activity_flag), true);
                createFunding.putExtra(mContext.getResources().getString(R.string.session_creation_with_new_group_flag), false);

                mContext.startActivity(createFunding);

                finish();
            }
        });

    }

    private void finish() {

        this.dismiss();
    }

    public void setCurUser(MoimingUserVO curUser) {
        this.curUser = curUser;
    }

    public void setCurGroup(MoimingGroupVO curGroup) {
        this.curGroup = curGroup;
    }

    public void setGroupMembers(List<MoimingMembersDTO> groupMembers) {
        this.curGroupMembers = groupMembers;
    }

}
