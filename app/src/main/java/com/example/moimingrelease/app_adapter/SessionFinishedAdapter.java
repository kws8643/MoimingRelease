package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionStatusMemberLinkerData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SessionFinishedAdapter extends RecyclerView.Adapter<SessionFinishedAdapter.FinishedSessionViewHolder> {

    private Context mContext;
    private boolean isMoimingUser;
    private MoimingSessionVO curSession;
    private String curUserUuid;
    private List<SessionStatusMemberLinkerData> dataList;

    public SessionFinishedAdapter(Context mContext, boolean isMoimingUser, MoimingSessionVO curSession, String curUserUuid, List<SessionStatusMemberLinkerData> dataList) {

        this.mContext = mContext;
        this.isMoimingUser = isMoimingUser;
        this.curSession = curSession;
        this.curUserUuid = curUserUuid;
        this.dataList = dataList;
    }

    @NonNull
    @NotNull
    @Override
    public FinishedSessionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_session_all_finished_item, parent, false);

        FinishedSessionViewHolder itemHolder = new FinishedSessionViewHolder(view);

        return itemHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FinishedSessionViewHolder holder, int position) {

        SessionStatusMemberLinkerData data = dataList.get(position);

        String name;
        String cost;

        if (isMoimingUser) {

            MoimingUserVO thisMember = data.getSelectedUser();

            if (curSession.getSessionCreatorUuid().toString().equals(thisMember.getUuid().toString())) {
                holder.imgCreator.setVisibility(View.VISIBLE);
            } else {
                holder.imgCreator.setVisibility(View.GONE);
            }

            if (curUserUuid.equals(thisMember.getUuid().toString())) {
                name = "나";
            } else {
                name = thisMember.getUserName();
            }

            cost = data.getPersonalCost() + " 원";

        } else {

            holder.imgCreator.setVisibility(View.GONE);

            NonMoimingUserVO thisNmu = data.getNmuUser();

            name = thisNmu.getNmuName();
            cost = thisNmu.getNmuPersonalCost() + " 원";

        }

        holder.textName.setText(name);
        holder.textCost.setText(cost);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class FinishedSessionViewHolder extends RecyclerView.ViewHolder {

        private TextView textName, textCost;
        private ImageView imgCreator;

        public FinishedSessionViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.text_all_finished_name);
            textCost = itemView.findViewById(R.id.text_all_finished_cost);
            imgCreator = itemView.findViewById(R.id.img_all_finished_creator);

        }
    }
}
