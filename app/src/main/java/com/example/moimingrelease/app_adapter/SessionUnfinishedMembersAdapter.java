package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.SessionCreatorNotificationCallBack;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionStatusMemberLinkerData;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SessionUnfinishedMembersAdapter extends RecyclerView.Adapter<SessionUnfinishedMembersAdapter.SessionUnfinishedMembersHolder> {

    private Context context;
    private List<SessionStatusMemberLinkerData> memberListData;
    private String curUserUuid;
    private MoimingSessionVO curSession;
    private SessionCreatorNotificationCallBack cbCallBack;

    private Map<UUID, CheckBox> checkBoxMap;
    private Map<UUID, LinearLayout> sentRequestBtnMap;


    public SessionUnfinishedMembersAdapter(Context context, List<SessionStatusMemberLinkerData> memberListData
            , String curUserUuid, MoimingSessionVO curSession, SessionCreatorNotificationCallBack cbCallBack) {

        this.context = context;
        this.memberListData = memberListData;

        this.curUserUuid = curUserUuid;
        this.curSession = curSession;
        this.cbCallBack = cbCallBack;

        checkBoxMap = new HashMap<>();
        sentRequestBtnMap = new HashMap<>();
    }

    @Override
    public SessionUnfinishedMembersHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_session_unfinished_item, parent, false);
        SessionUnfinishedMembersHolder itemholder = new SessionUnfinishedMembersHolder(view);

        return itemholder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SessionUnfinishedMembersHolder holder, int position) {

        SessionStatusMemberLinkerData membersLinkerData = memberListData.get(position);

        MoimingUserVO thisUser = membersLinkerData.getSelectedUser();

        holder.textName.setText(thisUser.getUserName());

        String text = membersLinkerData.getPersonalCost() + "원";
        holder.textPersonalCost.setText(text);

        holder.isFinished.setChecked(false);

        checkBoxMap.put(thisUser.getUuid(), holder.isFinished);
        holder.isFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox cb = (CheckBox) v;

                if (!cb.isChecked()) { // 체크 해제시

                    cbCallBack.changingUserState(thisUser.getUuid(), false, false);

                } else { // 체크시

                    cbCallBack.changingUserState(thisUser.getUuid(), true, false);

                }
            }
        });


        sentRequestBtnMap.put(thisUser.getUuid(), holder.btnSendRequest);
        // TODO: 해당 유저에게 송금 요청을 보냅니다
        holder.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbCallBack.sendFinishRequest(thisUser.getUuid(), membersLinkerData.getPersonalCost());

                changeRequestBtnStatus((LinearLayout) v);
            }
        });

    }

    public void changeAllRequestBtn() {

        for (LinearLayout btnRequest : sentRequestBtnMap.values()) {

            changeRequestBtnStatus(btnRequest);

        }

    }

    private void changeRequestBtnStatus(LinearLayout btnRequest) {

        btnRequest.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_light_main_bg, null));
        btnRequest.setEnabled(false);
    }


    public void checkConfirmingUser(List<UUID> stateChangedUserUuid) {

        /*for (int i = 0; i < stateChangedUserUuid.size(); i++) {

            CheckBox checkbox = checkBoxMap.get(stateChangedUserUuid.get(i));

            checkbox.setChecked(true);

        }*/
        Iterator<UUID> iterator = checkBoxMap.keySet().iterator();

        while (iterator.hasNext()) {

            UUID userUuid = iterator.next();
            if (stateChangedUserUuid.contains(userUuid)) {
                checkBoxMap.get(userUuid).setChecked(true);
            } else {
                checkBoxMap.get(userUuid).setChecked(false);
            }

        }
    }

    @Override
    public int getItemCount() {
        return memberListData.size();
    }

    class SessionUnfinishedMembersHolder extends RecyclerView.ViewHolder {

        private TextView textName, textPersonalCost;
        private LinearLayout btnSendRequest;
        private CheckBox isFinished;
//        private ImageView imgCreator;

        public SessionUnfinishedMembersHolder(@NonNull @NotNull View itemView) {

            super(itemView);

            textName = itemView.findViewById(R.id.text_unfinished_name);
            textPersonalCost = itemView.findViewById(R.id.text_unfinished_cost);
            btnSendRequest = itemView.findViewById(R.id.btn_send_request);
            isFinished = itemView.findViewById(R.id.checkbox_state_change);
//            imgCreator = itemView.findViewById(R.id.img_unfinished_creator);

        }
    }
}
