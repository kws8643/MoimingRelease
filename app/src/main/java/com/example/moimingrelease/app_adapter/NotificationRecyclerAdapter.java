package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.GroupActivity;
import com.example.moimingrelease.NotificationActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionActivity;
import com.example.moimingrelease.WelcomeActivity;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.response_dto.NotificationResponseDTO;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.NotificationViewHolder> {

    private Context mContext;
    private MoimingUserVO curUser;
    private List<MoimingGroupAndMembersDTO> groupAndMembersList;
    private List<ReceivedNotificationDTO> notificationList;

    public NotificationRecyclerAdapter(Context mContext, MoimingUserVO curUser, List<MoimingGroupAndMembersDTO> groupAndMembersList
            , List<ReceivedNotificationDTO> notificationList) {

        this.mContext = mContext;
        this.curUser = curUser;
        this.groupAndMembersList = groupAndMembersList;
        this.notificationList = notificationList;

    }

    @NonNull
    @NotNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_notification_recycler_item, parent, false);

        return new NotificationViewHolder(view);

    }

    /**
     * Notification Table
     * sentActivity 1. Group ??? ?????? ?????? ????????? ??????,
     * 2. Session has session data
     * 3. system has system data (no session, group data)
     */


    @Override
    public void onBindViewHolder(@NonNull @NotNull NotificationViewHolder holder, int position) {

        ReceivedNotificationDTO notificationData = notificationList.get(position);

        NotificationResponseDTO notiDto = notificationData.getNotification();

        String sentUserName = notificationData.getSentUserName();
        String msgContent = notiDto.getMsgText();

        // isRead Confirming
        if (notiDto.getRead()) {
            holder.layoutNotiItem.setBackgroundColor(mContext.getResources().getColor(R.color.moimingWhite, null));
        } else {
            holder.layoutNotiItem.setBackgroundColor(mContext.getResources().getColor(R.color.moimingNotificationTheme, null));
        }

       /* // First need to parse each notification. type
        if (notiDto.getSentActivity().equals("group")) { // From Group Activity

            String sentGroupName = notificationData.getSentGroupName();
            msgContent += notiDto.getMsgText();


        } else if (notiDto.getSentActivity().equals("session")) { // From Session Activity

            String sentGroupName = notificationData.getSentGroupName();
            String sentSessionName = notificationData.getSentSessionName();

            msgContent += sentGroupName + "??? ";
            msgContent += sentUserName + "?????? ";
            msgContent += sentSessionName + "????????? ";
            msgContent += notiDto.getMsgText();

        } else if (notiDto.getSentActivity().equals("system")) { // From Moiming (Notice,event)

            // TODO: ???????????? ????????? ??? ?????? ????????? ????????? ??????

        } else { // Does not exist

            System.out.println("ERROR");
        }
*/

        holder.textNotiContent.setText(msgContent);

        holder.textNotiDate.setText(setDateText(notiDto.getCreatedAtForm()));

        holder.layoutNotiItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent moveIntent;

                if (!notiDto.getSentActivity().equals("system")) {

                    MoimingGroupAndMembersDTO selectedGroup = getMatchingInfo(notiDto.getSentGroupUuid());

                    if (notiDto.getSentActivity().equals("group")) { // Group

                        moveIntent = new Intent(mContext, GroupActivity.class);

                        moveIntent.putExtra(mContext.getResources().getString(R.string.moiming_user_data_key), (Serializable) curUser);
                        moveIntent.putExtra(mContext.getResources().getString(R.string.moiming_group_and_members_data_key), selectedGroup);

                    } else { // Session

                        moveIntent = new Intent(mContext, GroupActivity.class);

                        moveIntent.putExtra(mContext.getResources().getString(R.string.moiming_user_data_key), (Serializable) curUser);
                        moveIntent.putExtra(mContext.getResources().getString(R.string.moiming_group_and_members_data_key), selectedGroup);
                        moveIntent.putExtra(mContext.getResources().getString(R.string.group_move_to_session_key), notiDto.getSentSessionUuid().toString());

                    }
                } else { // System // Type ??? ??? ???

                    moveIntent = new Intent(mContext, WelcomeActivity.class);

                    moveIntent.putExtra("first_user", false);

                }

                mContext.startActivity(moveIntent);
                ((NotificationActivity) mContext).finish();
            }
        });

    }

    private String setDateText(LocalDateTime createdDate) {

        LocalDateTime curTime = LocalDateTime.now();

        String textDate = "";

        int years = (int) ChronoUnit.YEARS.between(createdDate, curTime);
        int months = (int) ChronoUnit.MONTHS.between(createdDate, curTime);
        int days = (int) ChronoUnit.DAYS.between(createdDate, curTime);
        int hours = (int) ChronoUnit.HOURS.between(createdDate, curTime);
        int mins = (int) ChronoUnit.MINUTES.between(createdDate, curTime);


        if (mins < 60) {
            if (mins == 0) {
                textDate = "?????? ???";
            } else {
                textDate = mins + "??? ???";
            }
        } else { // ?????? ????????? ?????? 1?????? ~
            if (hours < 24) {
                textDate = hours + "?????? ???";
            } else { //??? ????????? ?????? 1 ??? ~
                if (days < 30) {
                    textDate = days + "??? ???";
                } else { // ??? ????????? ?????? 1??? ~
                    if (months < 12) {
                        textDate = months + "??? ???";
                    } else {
                        if (years == 0) textDate = "1??? ???";
                        else textDate = years + "??? ???";
                    }
                }
            }
        }

        return textDate;

    }

    private MoimingGroupAndMembersDTO getMatchingInfo(UUID sentGroupUuid) {

        for (int i = 0; i < groupAndMembersList.size(); i++) {

            MoimingGroupAndMembersDTO dto = groupAndMembersList.get(i);
            if (sentGroupUuid.equals(dto.getMoimingGroup().getUuid())) {

                return dto;
            }
        }

        Toast.makeText(mContext.getApplicationContext(), "???????????? ?????? ???????????????. ???????????? ??? ????????????", Toast.LENGTH_SHORT).show();

        return null;
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout layoutNotiItem;
        private ImageView imgNotiType;
        private TextView textNotiContent, textNotiDate;

        public NotificationViewHolder(@NonNull @NotNull View itemView) {

            super(itemView);

            layoutNotiItem = itemView.findViewById(R.id.layout_notification_item);
            imgNotiType = itemView.findViewById(R.id.img_notification_type);
            imgNotiType.setBackground(new ShapeDrawable(new OvalShape()));
            imgNotiType.setClipToOutline(true);

            textNotiContent = itemView.findViewById(R.id.text_notification_content);
            textNotiDate = itemView.findViewById(R.id.text_notification_date);

        }
    }
}
