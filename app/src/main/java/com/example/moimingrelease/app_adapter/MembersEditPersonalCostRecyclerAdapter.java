package com.example.moimingrelease.app_adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionCreationActivity;
import com.example.moimingrelease.app_listener_interface.SessionPersonalCostEditCntListener;
import com.example.moimingrelease.app_listener_interface.UserCostCancelCheckBoxListener;
import com.example.moimingrelease.app_listener_interface.UserEditPersonalCostListener;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionMemberLinkerData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MembersEditPersonalCostRecyclerAdapter extends RecyclerView.Adapter<MembersEditPersonalCostRecyclerAdapter.MembersEditViewHolder> {

    public static int MOIMING_EDIT_COST_USER_CNT = 0;

    private Context context;
    private int moimingCnt;

    private List<SessionMemberLinkerData> memberList;
    private UserEditPersonalCostListener editCostListener;
    private UserCostCancelCheckBoxListener cancelCheckBoxListener;
    private List<MembersEditViewHolder> membersViewList = new ArrayList<>();
    private SessionPersonalCostEditCntListener cntListener;


    public void setMoimingCnt(int moimingCnt) {

        this.moimingCnt = moimingCnt;


    }

    public MembersEditPersonalCostRecyclerAdapter(Context context, int moimingCnt, List<SessionMemberLinkerData> memberList, UserEditPersonalCostListener editCostListener
            , UserCostCancelCheckBoxListener cancelCheckBoxListener, SessionPersonalCostEditCntListener cntListener) {

        MOIMING_EDIT_COST_USER_CNT = 0;
        this.context = context;
        this.moimingCnt = moimingCnt;
        this.memberList = memberList;
        this.editCostListener = editCostListener;
        this.cancelCheckBoxListener = cancelCheckBoxListener;
        this.cntListener = cntListener;

    }

    @NonNull
    @NotNull
    @Override
    public MembersEditViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_invite_personal_cost_edit, parent, false);
        MembersEditViewHolder itemholder = new MembersEditViewHolder(view);

        return itemholder;
    }

    public void setMoimingEditCostUserCnt() {

        MOIMING_EDIT_COST_USER_CNT = 0;

        for (int i = 0; i < memberList.size(); i++) {

            SessionMemberLinkerData data = memberList.get(i);
            if (data.getIsEdited()) MOIMING_EDIT_COST_USER_CNT++;

        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MembersEditViewHolder holder, int position) {

        SessionMemberLinkerData memberDatas = memberList.get(position);
        holder.userName.setText(memberDatas.getUserName());

        int userCharge = memberDatas.getUserCost();
        holder.userCost.setText(String.valueOf(userCharge));
        Glide.with(context).load(memberDatas.getUserPfImg()).into(holder.imgUserProfile);

        holder.userCost.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

                //TODO: 설정 금액 초과시.
                String personalCostStr = view.getText().toString();

                if (personalCostStr.isEmpty()) {

                    Toast.makeText(view.getContext(), "해당 유저의 금액을 입력해주세요", Toast.LENGTH_SHORT).show();

                } else if (userCharge == Integer.parseInt(personalCostStr)) { //  동일한 금액을 입력했다고 알린다.

                    Toast.makeText(view.getContext(), "동일한 금액입니다", Toast.LENGTH_SHORT).show();

                } else {

                    int personalCost = Integer.parseInt(personalCostStr); // 바꾸려는 사람 금액.

                    // 누구를 얼마로 변경했는지 밖으로 전달한다.
                    editCostListener.onFinishEditing(true, position, personalCost, userCharge);

                    // 여기서 나머지 애들을 바꿔줘야 함.
                }

               /* MOIMING_EDIT_COST_USER_CNT = 0;

                for (int i = 0; i < memberList.size(); i++) {

                    SessionMemberLinkerData data = memberList.get(i);
                    if (data.getIsEdited()) MOIMING_EDIT_COST_USER_CNT++;

                }*/

                setMoimingEditCostUserCnt();

                cntListener.editUserCnt(true);

               /* Toast.makeText(context.getApplicationContext(), MOIMING_EDIT_COST_USER_CNT + ", "
                        + NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT + ", "
                        +String.valueOf(MOIMING_EDIT_COST_USER_CNT + NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT), Toast.LENGTH_SHORT).show();*/


                return false;
            }
        });


        holder.isEdited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) { // 선택 시

                    MOIMING_EDIT_COST_USER_CNT += 1;

                    if (MOIMING_EDIT_COST_USER_CNT + NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT >= moimingCnt) {

                        Toast.makeText(context.getApplicationContext(), "전원 수정은 불가합니다", Toast.LENGTH_SHORT).show();

                        MOIMING_EDIT_COST_USER_CNT -= 1;
                        cb.setChecked(false);

                    } else {
                        holder.userCost.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_edit_personal_cost, null));
                        holder.userCost.setEnabled(true);
                    }

                    /*Toast.makeText(context.getApplicationContext(), MOIMING_EDIT_COST_USER_CNT + ", "
                            + NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT + ", "
                            +String.valueOf(MOIMING_EDIT_COST_USER_CNT + NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT), Toast.LENGTH_SHORT).show();*/

                } else { // 해제 시

                    // 바뀐기 전이면 그냥 해제하면 됨.
//                    SessionCreationActivity.COST_EDIT_USER_CNT -= 1;
                    MOIMING_EDIT_COST_USER_CNT -= 1;

                    holder.userCost.setBackgroundColor(Color.TRANSPARENT);
                    holder.userCost.setText(String.valueOf(memberDatas.getUserCost()));
                    holder.userCost.setEnabled(false);
                    int prevFundingCost = memberDatas.getUserCost();


                    // 바뀐 후면 다시 Setting 들어가야 함.
                    // 누른 애가 해제되는 시점에서의 N빵으로 계산시키면 됨.
                    if (memberDatas.getIsEdited()) {
                        cancelCheckBoxListener.onCancelCheckbox(true, position, prevFundingCost);
                    }

                   /* Toast.makeText(context.getApplicationContext(), MOIMING_EDIT_COST_USER_CNT + ", "
                            + NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT + ", "
                            +String.valueOf(MOIMING_EDIT_COST_USER_CNT + NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT), Toast.LENGTH_SHORT).show();
                */}

            }
        });

        if (memberDatas.getIsEdited()) {

            holder.isEdited.setChecked(true);
            holder.userCost.setEnabled(true);
            holder.userCost.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_edit_personal_cost, null));

        } else {

            holder.isEdited.setChecked(false);
            holder.userCost.setEnabled(false);
            holder.userCost.setBackgroundColor(Color.TRANSPARENT);

        }

        if (memberDatas.isAutoChanged()) {
            holder.isAutoChanged.setVisibility(View.VISIBLE);
            memberDatas.setAutoChanged(false);
        } else {
            holder.isAutoChanged.setVisibility(View.GONE);
        }

        membersViewList.add(holder);

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    class MembersEditViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgUserProfile;
        private EditText userName, userCost;
        private CheckBox isEdited;
        private View isAutoChanged;

        public MembersEditViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imgUserProfile = itemView.findViewById(R.id.img_personal_profile);
            imgUserProfile.setBackground(new ShapeDrawable(new OvalShape()));
            imgUserProfile.setClipToOutline(true);

            userName = itemView.findViewById(R.id.text_personal_name);
            userName.setEnabled(false);


            // 일단 정해지는 금액대로만 하게 된다. Chckbox로 수정 가능.
            userCost = itemView.findViewById(R.id.input_personal_cost);
            userCost.setEnabled(false);

            isEdited = itemView.findViewById(R.id.is_cost_edited);
            isAutoChanged = itemView.findViewById(R.id.is_auto_changed);


        }

    }
}
