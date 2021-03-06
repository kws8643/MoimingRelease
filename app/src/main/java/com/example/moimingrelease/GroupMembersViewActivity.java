package com.example.moimingrelease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.view.GroupInfoMemberViewer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GroupMembersViewActivity extends AppCompatActivity {

    private MoimingUserVO curUser;
    private MoimingGroupVO curGroup;
    private ArrayList<MoimingMembersDTO> curGroupMembers;
    private Map<UUID, String> memberFcmTokenMap;
    private List<String> groupMembersUuid;

    private TextView textMemberCnt;

    private ConstraintLayout btnAddMembers;
    private GridLayout membersGrid;

    private void receiveIntent() {

        Intent receivedInfo = getIntent();

        if (receivedInfo.getExtras() != null) {

            curUser = (MoimingUserVO) receivedInfo.getSerializableExtra(getResources().getString(R.string.moiming_user_data_key));
            curGroup = receivedInfo.getExtras().getParcelable(getResources().getString(R.string.moiming_group_data_key));
            curGroupMembers = receivedInfo.getParcelableArrayListExtra(getResources().getString(R.string.group_members_data_key));
            groupMembersUuid = receivedInfo.getStringArrayListExtra("group_members_uuid");
            memberFcmTokenMap = (Map<UUID, String>) receivedInfo.getExtras().getSerializable(getResources().getString(R.string.fcm_token_map));

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_view);

        receiveIntent();


        initView();

        initParams();

        fillGridLayout();


        btnAddMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent inviteMembersActivity = new Intent(GroupMembersViewActivity.this, InviteGroupMembersActivity.class);

                inviteMembersActivity.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                inviteMembersActivity.putStringArrayListExtra("group_members_uuid", (ArrayList<String>) groupMembersUuid);
                inviteMembersActivity.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                inviteMembersActivity.putExtra(getResources().getString(R.string.fcm_token_map), (Serializable) memberFcmTokenMap);

                startActivityForResult(inviteMembersActivity, 100);
            }
        });


    }

    private void initView() {

        textMemberCnt = findViewById(R.id.text_group_member_cnt);
        String memberText = curGroup.getGroupMemberCnt() + " ???";
        textMemberCnt.setText(memberText);

        btnAddMembers = findViewById(R.id.btn_add_members);
        membersGrid = findViewById(R.id.group_members_grid);

    }


    private void initParams() {


    }

    private void fillGridLayout() {


        // 1) ?????? ?????????.
        GroupInfoMemberViewer curUserViewer = new GroupInfoMemberViewer(getApplicationContext());
        curUserViewer.setTextUserName(curUser.getUserName());
        curUserViewer.setImgUserPfImg(curUser.getUserPfImg());

        membersGrid.addView(curUserViewer);
        giveMarginInScrollView(curUserViewer);

        // 2) ?????? ???????????? ?????????.
        for (int i = 0; i < curGroupMembers.size(); i++) {

            MoimingMembersDTO members = curGroupMembers.get(i);

            GroupInfoMemberViewer memberViewer = new GroupInfoMemberViewer(getApplicationContext());
            memberViewer.setTextUserName(members.getUserName());
            memberViewer.setImgUserPfImg(members.getUserPfImg());

            membersGrid.addView(memberViewer);
            giveMarginInScrollView(memberViewer);

        }

    }

    private void giveMarginInScrollView(GroupInfoMemberViewer viewer) {

        GridLayout.LayoutParams params = (GridLayout.LayoutParams) viewer.getLayoutParams();

        params.setMargins(0, 0, dpToPx(24), dpToPx(30));

        viewer.setLayoutParams(params);

    }


    // dp -> pixel ????????? ??????
    private int dpToPx(int dp) {

        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                if (data != null) { // ????????? ?????????????????? FCM Map ??? ????????????.

                    List<String> addedUser = data.getStringArrayListExtra("added_user_uuid"); // ?????? ???????????? ????????? ??????.

                    Intent finishIntent = new Intent();

                    if (addedUser != null) {
                        finishIntent.putStringArrayListExtra("added_user_uuid", (ArrayList<String>) addedUser);
                        setResult(RESULT_OK, finishIntent);
                    } else {
                        setResult(RESULT_CANCELED);
                    }

                    finish();
                }
            } else { // ?????? ?????? ??????.

                finish();

            }
        }
    }


}