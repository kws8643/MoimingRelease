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
    private boolean isRecyclerClickable = true; // 기본은 되게끔

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

    public void alertItemList(List<MainRecyclerLinkerData> viewDataList, ArrayList<String> fixedGroupUuidList) {

        this.viewDataList = viewDataList;

        this.fixedGroupUuidList = fixedGroupUuidList;


    }

    public void setRecyclerClickable(boolean isClickable) {

        if (isClickable) {

            this.isRecyclerClickable = true;

        } else { // 잠시 리사이클러가 눌리는걸 금지

            this.isRecyclerClickable = false;

        }

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

            holder.imgNotice.setVisibility(View.GONE);

            Collections.sort(groupNotiList, byDate); // 날짜로 소팅한 상태에서

            ReceivedNotificationDTO recentNoti = null;

            for (int i = 0; i < groupNotiList.size(); i++) { // 돌리면서

                if (!groupNotiList.get(i).getNotification().getRead()) { // 하나라도 안읽은게 있다면

                    holder.imgNotice.setVisibility(View.VISIBLE); // 발견하자마자 그걸로 세팅한다
                    recentNoti = groupNotiList.get(i);
                    break;
                }
            }

            if (recentNoti == null) {
                recentNoti = groupNotiList.get(0);
            }

            String msg = recentNoti.getNotification().getMsgText();

            holder.mainRecyclerGroupRecentNotice.setText(msg);


        } else {
            holder.imgNotice.setVisibility(View.GONE);
            holder.mainRecyclerGroupRecentNotice.setText("해당 그룹의 최근 알림이 존재하지 않습니다");

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

                    if (isRecyclerClickable) {

                        int position = getAdapterPosition();

                        MainRecyclerLinkerData selectedGroup = viewDataList.get(position);  // 해당 그룹으로 이동!
                        MoimingGroupAndMembersDTO selectedGroupDatas = selectedGroup.getGroupData();

                        Intent toGroupActivity = new Intent(activityContext, GroupActivity.class);

                        toGroupActivity.putExtra(activityContext.getResources().getString(R.string.moiming_group_and_members_data_key), selectedGroupDatas);
                        toGroupActivity.putExtra(activityContext.getResources().getString(R.string.moiming_user_data_key), curUser);


                        activityContext.startActivity(toGroupActivity);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (isRecyclerClickable) {
                        int position = getAdapterPosition();

                        MainRecyclerLinkerData selectedGroup = viewDataList.get(position);

                        String groupUuid = selectedGroup.getGroupData().getMoimingGroup().getUuid().toString();

                        GroupLongTouchDialog dialog = new GroupLongTouchDialog(((MainActivity) activityContext)
                                , groupUuid, exitDialogListener, refreshListener, fixedGroupUuidList.contains(groupUuid));
                        dialog.show();

                    }
                    return false;
                }
            });


        }
    }

    Comparator<ReceivedNotificationDTO> byDate = new Comparator<ReceivedNotificationDTO>() {
        @Override
        public int compare(ReceivedNotificationDTO dto1, ReceivedNotificationDTO dto2) {
            return dto2.getNotification().getCreatedAtForm().compareTo(dto1.getNotification().getCreatedAtForm());
        }
    };

}
