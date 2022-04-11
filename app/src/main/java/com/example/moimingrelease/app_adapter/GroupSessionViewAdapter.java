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

        // 1. 내가 주인인가? 0 = 내 정산
        // 2. 진행중인 정산인가?
        // 3. 내가 보내야 하는가? 1 = 송금 필요 2 = 송금 완료 3 = 미참여
        // 4. 얼마를 보내야 하는가?

        SessionAndUserStatusDTO singleDTO = sessionList.get(position);
        MoimingSessionVO curSession = singleDTO.getMoimingSessionResponseDTO().convertToVO();

        if (curSession.getSessionType() == 1) { //더치페이

            holder.sessionType.setText("더치페이");
        } else {
            holder.sessionType.setText("모금");
        }

        holder.sessionName.setText(curSession.getSessionName());
        holder.sessionDate.setText(curSession.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));

        int curUserStatus = singleDTO.getCurUserStatus();


        if (!curSession.getFinished()) { // 완료되지 않은 정산활동 중
            /**
             0 = 내 정산 1 = 송금 필요 2 = 송금 완료 3 = 송금 확인 중 4 = 미참여
             */
            if (curUserStatus != 0) { // 현재 정산은 나의 정산이 아님

                holder.icCreator.setVisibility(View.GONE);

                if (curUserStatus == 1) { // 송금 필요, 송금 확정 전 상태

                    int curUserCost = singleDTO.getCurUserCost();

                    String actionString = singleDTO.getCreatorName() + "에게 송금하기";
                    holder.action.setText(actionString);
                    holder.action.setTextColor(context.getResources().getColor(R.color.moimingOrange, null));
                    holder.status.setText(String.valueOf(curUserCost) + " 원");
                    holder.status.setTextColor(context.getResources().getColor(R.color.moimingOrange, null));

                } else if (curUserStatus == 3) { // 송금 확인 중

                    int curUserCost = singleDTO.getCurUserCost();

                    String textStatus = "송금 확인 중";
                    String textAction = curUserCost + " 원";
                    holder.action.setText(textAction);
                    holder.status.setText(textStatus);
                    holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                    holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));


                } else if (curUserStatus == 2) { // 송금 완료

                    int curUserCost = singleDTO.getCurUserCost();

                    holder.action.setText("송금 완료");
                    holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                    holder.status.setText(String.valueOf(curUserCost) + " 원");
                    holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

                } else if (curUserStatus == 4) { // 미참여
                    holder.action.setText("미참여 활동");
                    holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                    holder.status.setText("미참여 활동");
                    holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                }


            } else { // 현재 정산은 나의 정산임.

                holder.icCreator.setVisibility(View.VISIBLE);
                holder.action.setText("송금 요청하기");
                holder.action.setTextColor(context.getResources().getColor(R.color.moimingTheme, null));

                String statusString = curSession.getSessionMemberCnt() + "명 중 " + curSession.getCurSenderCnt() + "명 완료";
                holder.status.setText(statusString);
                holder.status.setTextColor(context.getResources().getColor(R.color.moimingTheme, null));

            }

        } else { // 완료된 정산활동 입니다. // 완료 되었어도 나뉘어야 함

            if (curUserStatus == 0) {

                holder.icCreator.setVisibility(View.VISIBLE); // TODO 이거 색깔 회색으로?
                holder.action.setText("정산 완료");
                holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

                holder.status.setText("정산이 완료되었습니다");
                holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

            } else if (curUserStatus == 4) {

                holder.action.setText("미참여 활동");
                holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                holder.status.setText("정산이 완료되었습니다");
                holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

            } else { // curUserStatus == 2, 3 일 때, 내가 보낸 내역들!, 사실 여기서 3일 리는 없음.

                int curUserCost = singleDTO.getCurUserCost();

                String actionString = "정산 완료";
                holder.action.setText(actionString);
                holder.action.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));
                holder.status.setText(curUserCost + " 원");
                holder.status.setTextColor(context.getResources().getColor(R.color.textBoldGray, null));

            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //해당 SessionActivity 로 이동합니다.

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
