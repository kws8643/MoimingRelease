package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.UserCheckBoxListener;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteSessionGroupMembersRecyclerAdapter extends RecyclerView.Adapter<InviteSessionGroupMembersRecyclerAdapter.GroupMembersViewHolder> {

    private Context context;

    private List<MoimingMembersDTO> groupMembers;
    private List<String> checkedMemberList = new ArrayList<>(); // 아답터 notify 를 위해서임

    private UserCheckBoxListener checkBoxListener;
    private List<String> preInvitedMembersUuid;
    private boolean fromNewGroupCreation;

    private Map<String, CheckBox> checkBoxMap;

    public InviteSessionGroupMembersRecyclerAdapter(Context context, List<MoimingMembersDTO> groupMembers, UserCheckBoxListener checkBoxListener, List<String> preInvitedMembersUuid, boolean fromNewGroupCreation) {

        this.context = context;
        this.groupMembers = groupMembers;
        this.checkBoxListener = checkBoxListener;
        this.preInvitedMembersUuid = preInvitedMembersUuid;
        this.fromNewGroupCreation = fromNewGroupCreation;

        checkBoxMap = new HashMap<>();
    }


    @NonNull
    @NotNull
    @Override
    public GroupMembersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_user_invite_moiming, parent, false);
        InviteSessionGroupMembersRecyclerAdapter.GroupMembersViewHolder itemHolder = new InviteSessionGroupMembersRecyclerAdapter.GroupMembersViewHolder(view);

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupMembersViewHolder holder, int position) {

        MoimingMembersDTO member = groupMembers.get(position);

        checkBoxMap.put(member.getUuid().toString(), holder.memberIsChecked); // 모든 체크박스 대기

        holder.memberNameText.setText(member.getUserName());
        Glide.with(context).load(member.getUserPfImg()).into(holder.memberRecyclerImg);


        if(checkedMemberList.contains(member.getUuid().toString())){
            holder.memberIsChecked.setChecked(true);
        }else{
            holder.memberIsChecked.setChecked(false);
        }


        holder.memberIsChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox cb = (CheckBox) v;

                if(cb.isChecked()) {
                    checkedMemberList.add(member.getUuid().toString());
                }else{
                    checkedMemberList.remove(member.getUuid().toString());
                }
                checkBoxListener.onCheckBoxClick(cb.isChecked(), member);

            }
        });

        if (preInvitedMembersUuid.contains(member.getUuid().toString())) {

            holder.memberIsChecked.setChecked(true);
            holder.memberIsChecked.callOnClick();
            preInvitedMembersUuid.remove(member.getUuid().toString());
        }
    }

    public void removeMember(String memberUuid) {

        /*CheckBox selectedCheckBox = checkBoxMap.get(memberUuid);

        selectedCheckBox.performClick();*/
        checkedMemberList.remove(memberUuid);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

    class GroupMembersViewHolder extends RecyclerView.ViewHolder {

        private ImageView memberRecyclerImg;
        private ImageView kakaoLogoImg;
        private TextView memberNameText;
        private CheckBox memberIsChecked;

        public GroupMembersViewHolder(@NotNull View itemView) {
            super(itemView);

            memberRecyclerImg = itemView.findViewById(R.id.img_member_profile);
            memberRecyclerImg.setBackground(new ShapeDrawable(new OvalShape()));
            memberRecyclerImg.setClipToOutline(true);

            memberNameText = itemView.findViewById(R.id.text_member_name);
            memberIsChecked = itemView.findViewById(R.id.is_member_checked);
            kakaoLogoImg = itemView.findViewById(R.id.img_member_kakao_logo);
            if (!fromNewGroupCreation) {
                kakaoLogoImg.setVisibility(View.GONE);
            }

          /*  memberIsChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CheckBox cb = (CheckBox) view;

                    int position = getAdapterPosition();
                    GroupMembersDTO member = groupMembers.get(position);

                    Log.w("TAGTAGTAG", member.toString());

                    checkBoxListener.onCheckBoxClick(cb.isChecked(), member);

                }
            });
*/


        }
    }
}
