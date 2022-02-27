package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.SessionCreatorNmuCheckboxCallBack;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionStatusMemberLinkerData;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SessionUnfinishedNmusAdapter extends RecyclerView.Adapter<SessionUnfinishedNmusAdapter.SessionUnfinishedNmusHolder> {

    private Context context;
    private List<SessionStatusMemberLinkerData> nmuListData;
    private SessionCreatorNmuCheckboxCallBack nmuCallback;


    public SessionUnfinishedNmusAdapter(Context context, List<SessionStatusMemberLinkerData> nmuListData, SessionCreatorNmuCheckboxCallBack nmuCallback) {

        this.context = context;
        this.nmuListData = nmuListData;
        this.nmuCallback = nmuCallback;

    }


    @NonNull
    @NotNull
    @Override
    public SessionUnfinishedNmusHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_session_unfinished_item, parent, false);
        SessionUnfinishedNmusHolder itemholder = new SessionUnfinishedNmusHolder(view);

        return itemholder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SessionUnfinishedNmusHolder holder, int position) {

        SessionStatusMemberLinkerData nmuLinkerData = nmuListData.get(position);
        NonMoimingUserVO thisUser = nmuLinkerData.getNmuUser();

        holder.textName.setText(thisUser.getNmuName());
        holder.textPersonalCost.setText(String.valueOf(thisUser.getNmuPersonalCost()));

        holder.isNmuChecked.setChecked(false);
        holder.isNmuChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox cb = (CheckBox) v;

                if (!cb.isChecked()) { // 체크 해제시
                    nmuCallback.changingNmuState(thisUser.getUuid(), false);
                } else { // 체크시
                    nmuCallback.changingNmuState(thisUser.getUuid(), true);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return nmuListData.size();
    }

    class SessionUnfinishedNmusHolder extends RecyclerView.ViewHolder {

        private TextView textName, textPersonalCost;
        private LinearLayout btnSendRequest;
        private CheckBox isNmuChecked;


        public SessionUnfinishedNmusHolder(@NonNull @NotNull View itemView) {

            super(itemView);

            textName = itemView.findViewById(R.id.text_unfinished_name);
            textPersonalCost = itemView.findViewById(R.id.text_unfinished_cost);
            btnSendRequest = itemView.findViewById(R.id.btn_send_request);
            btnSendRequest.setVisibility(View.GONE);

            isNmuChecked = itemView.findViewById(R.id.checkbox_state_change);

        }
    }
}
