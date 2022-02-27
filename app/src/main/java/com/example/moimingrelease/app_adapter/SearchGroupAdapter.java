package com.example.moimingrelease.app_adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.GroupActivity;
import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.SearchGroupActivity;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.MainRecyclerLinkerData;
import com.example.moimingrelease.moiming_model.recycler_view_model.SearchedGroupViewData;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

public class SearchGroupAdapter extends RecyclerView.Adapter<SearchGroupAdapter.SearchGroupHolder> {

    private MoimingUserVO curUser;
    private Context context;
    private List<SearchedGroupViewData> searchedGroupData;

    public SearchGroupAdapter(Context context, MoimingUserVO curUser, List<SearchedGroupViewData> searchedGroupData) {

        this.context = context;
        this.curUser = curUser;
        this.searchedGroupData = searchedGroupData;

    }

    @NonNull
    @NotNull
    @Override
    public SearchGroupHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.view_main_search_group, parent, false);
        SearchGroupHolder viewHolder = new SearchGroupHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchGroupHolder holder, int position) {

        SearchedGroupViewData data = searchedGroupData.get(position);

        MoimingGroupVO dataGroup = data.getMoimingGroupAndMembers().getMoimingGroup();

        holder.groupName.setText(dataGroup.getGroupName());

        if (!data.isMemberSearched()) {

            holder.groupMembers.setVisibility(View.GONE);

        } else {

            holder.groupMembers.setVisibility(View.VISIBLE);

            String members = "> ";
            int membersSize = data.getSearchedGroupUsers().size();

            for (int i = 0; i < membersSize - 1; i++) {

                members += data.getSearchedGroupUsers().get(i) + ", ";

            }

            members += data.getSearchedGroupUsers().get(membersSize - 1);
            holder.groupMembers.setText(members);

        }

    }


    @Override
    public int getItemCount() {
        return searchedGroupData.size();
    }


    class SearchGroupHolder extends RecyclerView.ViewHolder {

        private TextView groupName;
        private TextView groupMembers;

        // 눌렀을때 GroupActivity 로 이동할 수 있도록 설정 필요
        public SearchGroupHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            groupName = itemView.findViewById(R.id.text_searched_group_name);
            groupMembers = itemView.findViewById(R.id.text_searched_group_member);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    Intent toGroupActivity = new Intent(context, GroupActivity.class);

                    toGroupActivity.putExtra(context.getResources().getString(R.string.moiming_group_and_members_data_key), searchedGroupData.get(position).getMoimingGroupAndMembers());
//                    toGroupActivity.putExtra(context.getResources().getString(R.string.moiming_group_data_key), (Serializable) searchedGroupData.get(position).getMoimingGroupVO());
                    toGroupActivity.putExtra(context.getResources().getString(R.string.moiming_user_data_key), curUser);

                    context.startActivity(toGroupActivity);
                    ((SearchGroupActivity) context).finish();
                }
            });


        }
    }

}
