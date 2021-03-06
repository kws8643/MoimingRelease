package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.moimingrelease.SessionActivity;
import com.example.moimingrelease.SessionCreationActivity;
import com.example.moimingrelease.moiming_model.extras.SessionAndUserStatusDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingSessionResponseDTO;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GroupSessionViewAdapter extends RecyclerView.Adapter<GroupSessionViewAdapter.GroupSessionViewHolder> {

    private Context context;
    private List<SessionAndUserStatusDTO> sessionList;
    private MoimingGroupVO curGroup;
    private MoimingUserVO curUser;

    public void setSessionList(List<SessionAndUserStatusDTO> sessionList) {

        this.sessionList = sessionList;
    }

    public List<SessionAndUserStatusDTO> getSessionList() {

        return this.sessionList;
    }

    public GroupSessionViewAdapter(Context context, List<SessionAndUserStatusDTO> sessionList, MoimingGroupVO curGroup, MoimingUserVO curUser) {

        this.context = context;
        this.sessionList = sessionList;
        this.curGroup = curGroup;
        this.curUser = curUser;
    }

    @NonNull
    @NotNull
    @Override
    public GroupSessionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.view_group_recycler_session_item, parent, false);
        GroupSessionViewHolder holder = new GroupSessionViewHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupSessionViewHolder holder, int position) {

        // 1. ?????? ????????????? 0 = ??? ??????
        // 2. ???????????? ?????????????
        // 3. ?????? ????????? ?????????? 1 = ?????? ?????? 2 = ?????? ?????? 3 = ?????????
        // 4. ????????? ????????? ??????????

        SessionAndUserStatusDTO singleDTO = sessionList.get(position);
        MoimingSessionVO curSession = singleDTO.getMoimingSessionResponseDTO().convertToVO();

        if (curSession.getSessionType() == 1) { //????????????

            holder.sessionType.setText("????????????");
        } else {
            holder.sessionType.setText("??????");
        }

        holder.sessionName.setText(curSession.getSessionName());
        holder.sessionDate.setText(curSession.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));

        int curUserStatus = singleDTO.getCurUserStatus();


        if (!curSession.getFinished()) { // ???????????? ?????? ???????????? ???
            /**
             0 = ??? ?????? 1 = ?????? ?????? 2 = ?????? ?????? 3 = ?????? ?????? ??? 4 = ?????????
             */
            if (curUserStatus != 0) { // ?????? ????????? ?????? ????????? ??????

                holder.icCreator.setVisibility(View.GONE);

                if (curUserStatus == 1) { // ?????? ??????, ?????? ?????? ??? ??????

                    String curUserCost = AppExtraMethods.moneyToWonWon(singleDTO.getCurUserCost()) + " ???";

                    String actionString = singleDTO.getCreatorName() + "?????? ????????????";
                    holder.action.setText(actionString);
                    holder.action.setTextColor(context.getResources().getColor(R.color.moimingOrange, null));
                    holder.status.setText(curUserCost);
                    holder.status.setTextColor(context.getResources().getColor(R.color.moimingOrange, null));

                } else if (curUserStatus == 3) { // ?????? ?????? ???

                    int curUserCost = singleDTO.getCurUserCost();

                    String textStatus = "?????? ?????? ???";
                    String textAction = AppExtraMethods.moneyToWonWon(curUserCost) + " ???";
                    holder.action.setText(textAction);
                    holder.status.setText(textStatus);
                    holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                    holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));


                } else if (curUserStatus == 2) { // ?????? ??????

                    String curUserCost = AppExtraMethods.moneyToWonWon(singleDTO.getCurUserCost()) +  " ???";

                    holder.action.setText("?????? ??????");
                    holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                    holder.status.setText(curUserCost);
                    holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

                } else if (curUserStatus == 4) { // ?????????
                    holder.action.setText("????????? ??????");
                    holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                    holder.status.setText("????????? ??????");
                    holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                }


            } else { // ?????? ????????? ?????? ?????????.

                holder.icCreator.setVisibility(View.VISIBLE);
                holder.action.setText("?????? ????????????");
                holder.action.setTextColor(context.getResources().getColor(R.color.moimingTheme, null));

                String statusString = curSession.getSessionMemberCnt() + "??? ??? " + curSession.getCurSenderCnt() + "??? ??????";
                holder.status.setText(statusString);
                holder.status.setTextColor(context.getResources().getColor(R.color.moimingTheme, null));

            }

        } else { // ????????? ???????????? ?????????. // ?????? ???????????? ???????????? ???

            if (curUserStatus == 0) {

                holder.icCreator.setVisibility(View.VISIBLE); // TODO ?????? ?????? ?????????????
                holder.action.setText("?????? ??????");
                holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

                holder.status.setText("????????? ?????????????????????");
                holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

            } else if (curUserStatus == 4) {

                holder.action.setText("????????? ??????");
                holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                holder.status.setText("????????? ?????????????????????");
                holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

            } else { // curUserStatus == 2, 3 ??? ???, ?????? ?????? ?????????!, ?????? ????????? 3??? ?????? ??????.

                int curUserCost = singleDTO.getCurUserCost();

                String actionString = "?????? ??????";
                holder.action.setText(actionString);
                holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                holder.status.setText(AppExtraMethods.moneyToWonWon(curUserCost) + " ???");
                holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????? SessionActivity ??? ???????????????.

                Intent toSession = new Intent(context, SessionActivity.class);

                toSession.putExtra("cur_user_status", curUserStatus);
                toSession.putExtra(context.getResources().getString(R.string.moiming_user_data_key), curUser);
                toSession.putExtra(context.getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                toSession.putExtra(GroupActivity.MOIMING_SESSION_DATA_KEY, curSession);

                context.startActivity(toSession);


            }
        });

    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }


    class GroupSessionViewHolder extends RecyclerView.ViewHolder {

        private TextView sessionName, sessionDate, sessionType, status, action;
        private ImageView icCreator;

        public GroupSessionViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            sessionName = itemView.findViewById(R.id.text_session_name);
            sessionDate = itemView.findViewById(R.id.text_session_date);
            sessionType = itemView.findViewById(R.id.text_session_type);
            status = itemView.findViewById(R.id.text_status);
            action = itemView.findViewById(R.id.text_action);
            icCreator = itemView.findViewById(R.id.img_ic_creator);

        }
    }
}
