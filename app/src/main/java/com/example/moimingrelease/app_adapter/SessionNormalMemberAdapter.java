package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class SessionNormalMemberAdapter extends RecyclerView.Adapter<SessionNormalMemberAdapter.SessionNormalMemberViewHolder> {

    private Context context;
    private boolean isMoimingMember;
    private List<SessionStatusMemberLinkerData> dataList;
    private String curUserUuid;
    private MoimingSessionVO curSession;

    public SessionNormalMemberAdapter(Context context, List<SessionStatusMemberLinkerData> dataList
            , boolean isMoimingMember, String curUserUuid, MoimingSessionVO curSession) {

        this.context = context;
        this.dataList = dataList;
        this.isMoimingMember = isMoimingMember;
        this.curUserUuid = curUserUuid;
        this.curSession = curSession;

    }


    @NonNull
    @NotNull
    @Override
    public SessionNormalMemberViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View viewHolder = LayoutInflater.from(context).inflate(R.layout.view_session_normal_member, parent, false);
        SessionNormalMemberViewHolder itemViewHolder = new SessionNormalMemberViewHolder(viewHolder);

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SessionNormalMemberViewHolder holder, int position) {

        SessionStatusMemberLinkerData memberData = dataList.get(position);

        String name;
        String cost;

        if(isMoimingMember){

            MoimingUserVO thisMember = memberData.getSelectedUser();
            
            if(thisMember.getUuid().toString().equals(curUserUuid)){ // 나임
                
                name = "나";
                
            }else{

                name = thisMember.getUserName();
            }
            
            if(thisMember.getUuid().toString().equals(curSession.getSessionCreatorUuid().toString())){ // 총무임
                
                holder.imgCreator.setVisibility(View.VISIBLE);

            }else{

                holder.imgCreator.setVisibility(View.GONE);

            }
            
            cost = AppExtraMethods.moneyToWonWon(memberData.getPersonalCost()) + " 원";

        }else{

            NonMoimingUserVO thisMember = memberData.getNmuUser();

            name = thisMember.getNmuName();
            cost = AppExtraMethods.moneyToWonWon(thisMember.getNmuPersonalCost()) + " 원";

            holder.imgCreator.setVisibility(View.GONE);

        }

        holder.textName.setText(name);
        holder.textCost.setText(cost);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class SessionNormalMemberViewHolder extends RecyclerView.ViewHolder {

        private TextView textName, textCost;
        private ImageView imgCreator;

        public SessionNormalMemberViewHolder(@NonNull @NotNull View itemView) {

            super(itemView);

            textName = itemView.findViewById(R.id.text_normal_name);
            textCost = itemView.findViewById(R.id.text_normal_cost);
            imgCreator = itemView.findViewById(R.id.img_normal_creator);

        }
    }
}
