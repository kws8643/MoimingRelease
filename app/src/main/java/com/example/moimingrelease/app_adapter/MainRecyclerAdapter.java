package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.GroupActivity;
import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.MainFixedGroupRefreshListener;
import com.example.moimingrelease.app_listener_interface.GroupExitDialogListener;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.NotificationVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.MainRecyclerLinkerData;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.dialog.GroupLongTouchDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// 유저 그룹 리스트 아답터
public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MainRecyclerViewHolder> {

    private Context activityContext;
    private List<MainRecyclerLinkerData> viewDataList;
    private MoimingUserVO curUser;
    private GroupExitDialogListener exitDialogListener;
    private MainFixedGroupRefreshListener refreshListener;
    private ArrayList<String> fixedGroupUuidList;


    public MainRecyclerAdapter(Context activityContext, List<MainRecyclerLinkerData> viewDataList
            , MoimingUserVO curUser, GroupExitDialogListener exitDialogListener
            , MainFixedGroupRefreshListener refreshListener, ArrayList<String> fixedGroupUuidList) {

        this.activityContext = activityContext;
        this.viewDataList = viewDataList;
        this.curUser = curUser;
        this.exitDialogListener = exitDialogListener;
        this.refreshListener = refreshListener;
        this.fixedGroupUuidList = fixedGroupUuidList;

    }

    @NonNull
    @NotNull
    @Override
    public MainRecyclerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activityContext).inflate(R.layout.view_main_recycler_group_item, parent, false);
        MainRecyclerViewHolder itemHolder = new MainRecyclerViewHolder(view);

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MainRecyclerViewHolder holder, int position) {

        MainRecyclerLinkerData itemData = viewDataList.get(position);

        List<ReceivedNotificationDTO> groupNotiList = itemData.getGroupNotification();

        holder.mainRecyclerGroupName.setText(itemData.getGroupData().getMoimingGroup().getGroupName());

        String groupUuid = itemData.getGroupData().getMoimingGroup().getUuid().toString();
        if (fixedGroupUuidList.contains(groupUuid)) {
            holder.imgPinned.setVisibility(View.VISIBLE);
        } else {
            holder.imgPinned.setVisibility(View.GONE);

        }

        if (!groupNotiList.isEmpty()) {
            holder.imgNotice.setVisibility(View.VISIBLE);

            Collections.sort(groupNotiList, byDate);

            ReceivedNotificationDTO recentNoti = groupNotiList.get(0);

            String msg = recentNoti.getSentUserName() + "님이 " + recentNoti.getNotification().getMsgText();

            holder.mainRecyclerGroupRecentNotice.setText(msg);


        } else {
            holder.imgNotice.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return viewDataList.size();
    }

    class MainRecyclerViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPinned, imgNotice;
        private ImageView mainRecyclerGroupImg;
        private TextView mainRecyclerGroupName;
        private TextView mainRecyclerGroupRecentNotice;


        public MainRecyclerViewHolder(@NotNull View itemView) {
            super(itemView);

            mainRecyclerGroupImg = itemView.findViewById(R.id.main_recycler_group_img);
            mainRecyclerGroupImg.setBackground(new ShapeDrawable(new OvalShape()));
            mainRecyclerGroupImg.setClipToOutline(true);

            mainRecyclerGroupName = itemView.findViewById(R.id.main_recycler_group_name);
            mainRecyclerGroupRecentNotice = itemView.findViewById(R.id.main_recycler_group_recent_notice);

            imgPinned = itemView.findViewById(R.id.img_is_pinned);
            imgPinned.setVisibility(View.GONE);
            imgNotice = itemView.findViewById(R.id.img_notice_exist);
            imgNotice.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();

                    MainRecyclerLinkerData selectedGroup = viewDataList.get(position);  // 해당 그룹으로 이동!
                    MoimingGroupAndMembersDTO selectedGroupDatas = selectedGroup.getGroupData();
                    MoimingGroupVO selectedGroupData = selectedGroup.getGroupData().getMoimingGroup();

                    Intent toGroupActivity = new Intent(activityContext, GroupActivity.class);

                    toGroupActivity.putExtra(activityContext.getResources().getString(R.string.moiming_group_and_members_data_key), selectedGroupDatas);
                    toGroupActivity.putExtra(activityContext.getResources().getString(R.string.moiming_user_data_key), curUser);

                    Log.w(MainActivity.MAIN_TAG, selectedGroupData.toString());

                    activityContext.startActivity(toGroupActivity);

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int position = getAdapterPosition();

                    MainRecyclerLinkerData selectedGroup = viewDataList.get(position);

                    String groupUuid = selectedGroup.getGroupData().getMoimingGroup().getUuid().toString();

                    GroupLongTouchDialog dialog = new GroupLongTouchDialog(((MainActivity) activityContext)
                            , groupUuid, exitDialogListener, refreshListener, fixedGroupUuidList.contains(groupUuid));
                    dialog.show();

                    return false;
                }
            });


        }
    }

    Comparator<ReceivedNotificationDTO> byDate = new Comparator<ReceivedNotificationDTO>() {
        @Override
        public int compare(ReceivedNotificationDTO dto1, ReceivedNotificationDTO dto2) {
            return dto1.getNotification().getCreatedAtForm().compareTo(dto2.getNotification().getCreatedAtForm());
        }
    };

}
