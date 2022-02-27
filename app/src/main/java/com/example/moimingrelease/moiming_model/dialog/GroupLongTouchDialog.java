package com.example.moimingrelease.moiming_model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.MainFixedGroupRefreshListener;
import com.example.moimingrelease.app_listener_interface.GroupExitDialogListener;

import org.json.JSONArray;
import org.json.JSONException;

public class GroupLongTouchDialog extends Dialog {

    private Context mContext;

    private LinearLayout btnGroupFix, btnGroupUnfix, btnGroupExit;
    private String groupUuid;
    private boolean isFixed;

    private GroupExitDialogListener exitDialogListener;
    private MainFixedGroupRefreshListener refreshListener;

    SharedPreferences fixedGroupSp;

    // 여기에서 하는 moiming_user_db 에서의 일은 insert / create 밖에 없음!

    public GroupLongTouchDialog(Context mContext, String groupUuid
            , GroupExitDialogListener exitDialogListener, MainFixedGroupRefreshListener refreshListener
            , boolean isFixed) {

        super(mContext);
        this.mContext = mContext;
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.groupUuid = groupUuid;
        this.exitDialogListener = exitDialogListener;
        this.refreshListener = refreshListener;
        this.isFixed = isFixed;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_group_long_touch);

        btnGroupFix = findViewById(R.id.btn_group_fix);
        btnGroupUnfix = findViewById(R.id.btn_group_unfix);
        btnGroupExit = findViewById(R.id.btn_group_exit);

        if (isFixed) {
            btnGroupUnfix.setVisibility(View.VISIBLE);
            btnGroupFix.setVisibility(View.GONE);
        } else {
            btnGroupUnfix.setVisibility(View.GONE);
            btnGroupFix.setVisibility(View.VISIBLE);
        }

        btnGroupFix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fixedGroupSp = getContext().getSharedPreferences(MainActivity.FIXED_GROUP_SP_NAME, Context.MODE_PRIVATE);

                String jsonGroupUuids = fixedGroupSp.getString(MainActivity.FIXED_GROUP_UUID_KEY, "");
                SharedPreferences.Editor spEdit = fixedGroupSp.edit();

                if (jsonGroupUuids.equals("")) { // 처음 저장하는 것

                    JSONArray jsonUuidArray = new JSONArray();
                    jsonUuidArray.put(groupUuid);
                    spEdit.putString(MainActivity.FIXED_GROUP_UUID_KEY, jsonUuidArray.toString());
                    // 처음 등록된 애를 저장한다.

                } else { // 저장하던 Array 가 있음.

                    try {

                        JSONArray jsonUuidArray = new JSONArray(jsonGroupUuids);
                        jsonUuidArray.put(groupUuid);
                        spEdit.putString(MainActivity.FIXED_GROUP_UUID_KEY, jsonUuidArray.toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                spEdit.apply();

                refreshListener.refreshMainGroup();

                finish();
            }
        });

        btnGroupUnfix.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {

                // 그냥 바로 SP 에서 빼주고, Refresh 하도록 도와주면 된다.
                fixedGroupSp = getContext().getSharedPreferences(MainActivity.FIXED_GROUP_SP_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor spEdit = fixedGroupSp.edit();

                String fixedGroupUuids = fixedGroupSp.getString(MainActivity.FIXED_GROUP_UUID_KEY, "");

                try {

                    JSONArray jsonArray = new JSONArray(fixedGroupUuids);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        String iteratorUuid = jsonArray.optString(i);

                        if (iteratorUuid.equals(groupUuid)) {
                            jsonArray.remove(i);
                        }
                    }

                    spEdit.putString(MainActivity.FIXED_GROUP_UUID_KEY, jsonArray.toString()); // 다시 저장.

                } catch (JSONException e) {

                    e.printStackTrace();
                }

                spEdit.apply();

                refreshListener.refreshMainGroup();

                finish();
            }
        });


        btnGroupExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exitDialogListener.initExitConfirmDialog(false, groupUuid); // 아직 확정은 아님.

                finish();

            }
        });

    }

    private void finish() {

        this.dismiss();
    }


}
