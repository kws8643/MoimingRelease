package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.SessionCreationViewOldGroupListener;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SessionCreationOldGroupsAdapter extends RecyclerView.Adapter<SessionCreationOldGroupsAdapter.OldGroupHolder> {

    private Context context;
    private ArrayList<MoimingGroupAndMembersDTO> groupList;
    private SessionCreationViewOldGroupListener chooseGroupListener;

    public SessionCreationOldGroupsAdapter(Context context, ArrayList<MoimingGroupAndMembersDTO> groupList, SessionCreationViewOldGroupListener chooseGroupListener) {

        this.context = context;
        this.groupList = groupList;
        this.chooseGroupListener = chooseGroupListener;

    }

    @NonNull
    @NotNull
    @Override
    public OldGroupHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_session_creation_choose_old_group_item, parent, false);

        return new OldGroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OldGroupHolder holder, int position) {

        MoimingGroupAndMembersDTO data = groupList.get(position);

        MoimingGroupVO groupData = data.getMoimingGroup();

        holder.textGroupName.setText(groupData.getGroupName());
        holder.layoutOldGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseGroupListener.moveToViewGroup(groupData, data.getMoimingMembersList());
            }
        });


    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    class OldGroupHolder extends RecyclerView.ViewHolder {

        private ImageView imgGroupPf;
        private TextView textGroupName;
        private ConstraintLayout layoutOldGroup;

        public OldGroupHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imgGroupPf = itemView.findViewById(R.id.img_group_pf);
            imgGroupPf.setBackground(new ShapeDrawable(new OvalShape()));
            imgGroupPf.setClipToOutline(true);

            textGroupName = itemView.findViewById(R.id.text_group_name_choose_old_group);
            layoutOldGroup = itemView.findViewById(R.id.layout_old_group);


        }

    }
}
