package com.example.moimingrelease.app_adapter;

import android.app.Notification;
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
import com.example.moimingrelease.moiming_model.response_dto.NotificationResponseDTO;

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

                    holder.imgNotice.setVisibility(View.VISIBLE);
                    recentNoti = groupNotiList.get(i); // 발견하자마자 그걸로 세팅한다
                    break;
                }
            }

            if (recentNoti == null) {
                recentNoti = groupNotiList.get(0);
            }

            String strMsg = parseNoticeText(recentNoti);

            holder.mainRecyclerGroupRecentNotice.setText(strMsg);


        } else {
            holder.imgNotice.setVisibility(View.GONE);
            holder.mainRecyclerGroupRecentNotice.setText("해당 그룹의 최근 알림이 존재하지 않습니다");

        }

    }


    /**
     * 알림 종류: sentActivity = group / session
     * <p>
     * group
     * 0) 내가 있는 그룹에 (누군가)가 (새유저)를 초대함
     * 1) (누군가)가 (*나*)를 (새로운 그룹)에 초대함
     * 2) (기존 그룹)의 회계장부에 변경사항이 생김
     * <p>
     * session
     * 1) (총무)가 (*나*) 에게 (정산활동)에 대한 정산요청을 보냄
     * 2) (정산참여인원)이 (나:총무)에게 정산확인 요청을 보냄
     */


    private String parseNoticeText(ReceivedNotificationDTO recentNoti) {

        NotificationResponseDTO sentNoti = recentNoti.getNotification();

        if (sentNoti.getSentActivity().equals("group")) {

            String strMsg = "";
            String inviter = recentNoti.getSentUserName();


            switch (sentNoti.getMsgType()) {
                case 0:

                    int index = sentNoti.getMsgText().indexOf("이");
                    String invitedMembers = sentNoti.getMsgText().substring(0, index);

                    strMsg += invitedMembers;
                    strMsg += "이 " + inviter + "님에게 초대됨";
                    break;

                case 1:
                    strMsg = "회원님이 " + inviter + "님에게 초대됨";
                    break;

                case 2:

                    String changer = recentNoti.getSentUserName();
                    strMsg = changer + "님이 회계장부 내역을 ";
                    if (sentNoti.getMsgText().contains("추가")) {
                        strMsg += "추가";
                    } else if (sentNoti.getMsgText().contains("수정")) {
                        strMsg += "수정";
                    } else { // 삭제
                        strMsg += "삭제";
                    }
                    break;

            }

            return strMsg;


        } else if (sentNoti.getSentActivity().equals("session")) {

            String strMsg = "";
            String sessionName = recentNoti.getSentSessionName();
            String sender = recentNoti.getSentUserName();

            switch (sentNoti.getMsgType()) {
                case 1:
                    strMsg = sender + "님의 '" + sessionName + "' 정산 요청";
                    break;
                case 2:
                    strMsg = sender + "님의 '" + sessionName + "' 송금 확인 요청";
                    break;
            }

            return strMsg;

        } else { // 이샹한게 여기로 들어옴.
            return recentNoti.getNotification().getMsgText();
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
