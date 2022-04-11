package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.moiming_model.recycler_view_model.PreviousSessionMemberStatusData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PreviousFundingMemberInfoAdapter extends RecyclerView.Adapter<PreviousFundingMemberInfoAdapter.MemberInfoViewHolder> {

    private Context context;
    private List<PreviousSessionMemberStatusData> memberStatusList;


    public PreviousFundingMemberInfoAdapter(Context context, List<PreviousSessionMemberStatusData> memberStatusList) {

        this.context = context;
        this.memberStatusList = memberStatusList;
    }


    @NonNull
    @NotNull
    @Override
    public MemberInfoViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_prev_funding_member_status, parent, false);
        MemberInfoViewHolder viewHolder = new MemberInfoViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MemberInfoViewHolder holder, int position) {

        PreviousSessionMemberStatusData sessionMemberData =  memberStatusList.get(position);

        holder.textMemberName.setText(sessionMemberData.getUserName());
        holder.textMemberCost.setText(String.valueOf(sessionMemberData.getUserCost()) + " 원");

    }

    @Override
    public int getItemCount() {
        return memberStatusList.size();
    }

    class MemberInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView textMemberName, textMemberCost;

        public MemberInfoViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textMemberName = itemView.findViewById(R.id.text_funding_member_name);
            textMemberCost = itemView.findViewById(R.id.text_funding_member_cost);


        }
    }
}
