package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.SessionCreatorSentCheckboxCallBack;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionStatusMemberLinkerData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SessionFinishedMembersAdapter extends RecyclerView.Adapter<SessionFinishedMembersAdapter.SessionFinishedMembersHolder> {

    private Context context;
    private List<SessionStatusMemberLinkerData> sessionMemberList;
    private boolean isMoimingUser;
    private String curUserUuid;
    private MoimingSessionVO curSession;
    private SessionCreatorSentCheckboxCallBack sentUserCallback;


    public SessionFinishedMembersAdapter(Context context, List<SessionStatusMemberLinkerData> sessionMemberList
            , boolean isMoimingUser, String curUserUuid, MoimingSessionVO curSession, SessionCreatorSentCheckboxCallBack sentUserCallback) {

        this.context = context;
        this.sessionMemberList = sessionMemberList;
        this.isMoimingUser = isMoimingUser;
        this.curUserUuid = curUserUuid;
        this.curSession = curSession;
        this.sentUserCallback = sentUserCallback;

    }


    @NonNull
    @NotNull
    @Override
    public SessionFinishedMembersHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_session_finished_item, parent, false);

        SessionFinishedMembersHolder itemHolder = new SessionFinishedMembersHolder(view);

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SessionFinishedMembersHolder holder, int position) {

        SessionStatusMemberLinkerData memberData = sessionMemberList.get(position);

        String name;
        String cost;

        holder.isNotSent.setChecked(false);

        if (isMoimingUser) {

            MoimingUserVO thisMember = memberData.getSelectedUser();

            if (thisMember.getUuid().toString().equals(curUserUuid)) { // 나임

                name = "나";

            } else {

                name = thisMember.getUserName();
            }

            if (thisMember.getUuid().toString().equals(curSession.getSessionCreatorUuid().toString())) { // 총무임

                holder.imgCreator.setVisibility(View.VISIBLE);
                holder.isNotSent.setVisibility(View.GONE);

            } else {

                holder.imgCreator.setVisibility(View.GONE);
                holder.isNotSent.setVisibility(View.VISIBLE);

            }

            cost = AppExtraMethods.moneyToWonWon(memberData.getPersonalCost()) + " 원";

            holder.isNotSent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckBox cb = (CheckBox) v;

                    if (!cb.isChecked()) { // 체크 해제시
                        sentUserCallback.changeSentUserState(thisMember.getUuid(), false, true);
                    } else { // 체크시
                        sentUserCallback.changeSentUserState(thisMember.getUuid(), true, true);
                    }
                }
            });

        } else {

            NonMoimingUserVO thisMember = memberData.getNmuUser();

            name = thisMember.getNmuName();
            cost = AppExtraMethods.moneyToWonWon(thisMember.getNmuPersonalCost()) + " 원";

            holder.imgCreator.setVisibility(View.GONE);

            holder.isNotSent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckBox cb = (CheckBox) v;

                    if (!cb.isChecked()) { // 체크 해제시
                        sentUserCallback.changeSentUserState(thisMember.getUuid(), false, false);
                    } else { // 체크시
                        sentUserCallback.changeSentUserState(thisMember.getUuid(), true, false);
                    }
                }
            });

        }

        holder.textName.setText(name);
        holder.textCost.setText(cost);

    }

    @Override
    public int getItemCount() {
        return sessionMemberList.size();
    }

    class SessionFinishedMembersHolder extends RecyclerView.ViewHolder {

        private CheckBox isNotSent; // 미완료로 보내는 체크
        private TextView textName;
        private TextView textCost;
        private ImageView imgCreator;

        public SessionFinishedMembersHolder(@NonNull @NotNull View itemView) {

            super(itemView);

            isNotSent = itemView.findViewById(R.id.checkbox_is_unfinished);
            textName = itemView.findViewById(R.id.text_finished_name);
            textCost = itemView.findViewById(R.id.text_finished_cost);
            imgCreator = itemView.findViewById(R.id.img_finished_creator);

        }
    }
}
