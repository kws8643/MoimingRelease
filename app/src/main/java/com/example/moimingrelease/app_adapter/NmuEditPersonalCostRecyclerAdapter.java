package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionCreationActivity;
import com.example.moimingrelease.app_listener_interface.SessionCreationNmuNameChangeListener;
import com.example.moimingrelease.app_listener_interface.SessionPersonalCostEditCntListener;
import com.example.moimingrelease.app_listener_interface.UserCostCancelCheckBoxListener;
import com.example.moimingrelease.app_listener_interface.UserEditPersonalCostListener;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionMemberLinkerData;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionNmuLinkerData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NmuEditPersonalCostRecyclerAdapter extends RecyclerView.Adapter<NmuEditPersonalCostRecyclerAdapter.NmuEditViewHolder> {

    public static int NMU_EDIT_COST_USER_CNT = 0;

    private Context context;
    private int moimingCnt;
    private List<SessionNmuLinkerData> nmuList;
    private UserEditPersonalCostListener editCostListener;
    private UserCostCancelCheckBoxListener cancelCheckBoxListener;
    private SessionPersonalCostEditCntListener cntListener;
    private SessionCreationNmuNameChangeListener nmuNameChangeListener;

    private int cntChecked = 0;

    public NmuEditPersonalCostRecyclerAdapter(Context context, int moimingCnt, List<SessionNmuLinkerData> nmuList
            , UserEditPersonalCostListener editCostListener, UserCostCancelCheckBoxListener cancelCheckBoxListener
            , SessionPersonalCostEditCntListener cntListener, SessionCreationNmuNameChangeListener nmuNameChangeListener) {

        NMU_EDIT_COST_USER_CNT = 0;
        this.context = context;
        this.nmuList = nmuList;
        this.moimingCnt = moimingCnt;
        this.editCostListener = editCostListener;
        this.cancelCheckBoxListener = cancelCheckBoxListener;
        this.cntListener = cntListener;
        this.nmuNameChangeListener = nmuNameChangeListener;

    }

    @NonNull
    @NotNull
    @Override
    public NmuEditViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_invite_personal_cost_edit, parent, false);
        NmuEditViewHolder itemholder = new NmuEditViewHolder(view);

        return itemholder;
    }

    public void setNmuEditCostUserCnt() {

        NMU_EDIT_COST_USER_CNT = 0;

        for (int i = 0; i < nmuList.size(); i++) {

            SessionNmuLinkerData data = nmuList.get(i);
            if (data.getIsEdited()) NMU_EDIT_COST_USER_CNT++;

        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NmuEditViewHolder holder, int position) {

        SessionNmuLinkerData nmuDatas = nmuList.get(position);
        holder.userName.setText(nmuDatas.getUserName());

        holder.userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // ????????? ?????? --> Main ?????? ????????? ???????????????.
                String curNmuUserName = holder.userName.getText().toString();
                nmuNameChangeListener.saveNmuName(position, curNmuUserName);
            }
        });

        int userCharge = nmuDatas.getUserCost();
        holder.userCost.setText(AppExtraMethods.moneyToWonWon(userCharge));


        holder.userCost.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

                String personalCostStr = AppExtraMethods.wonToNormal(view.getText().toString());

                if (personalCostStr.isEmpty()) {

                    Toast.makeText(view.getContext(), "?????? ????????? ????????? ??????????????????", Toast.LENGTH_SHORT).show();

                } else if (userCharge == Integer.parseInt(personalCostStr)) { //  ????????? ????????? ??????????????? ?????????.

                    Toast.makeText(view.getContext(), "????????? ???????????????", Toast.LENGTH_SHORT).show();

                } else {

                    int personalCost = Integer.parseInt(personalCostStr); // ???????????? ?????? ??????.

                    // ????????? ????????? ??????????????? ????????? ????????????.
                    editCostListener.onFinishEditing(false
                            , position, personalCost, userCharge);
                }

                setNmuEditCostUserCnt();

                cntListener.editUserCnt(false);

                return false;
            }
        });

        holder.isEdited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) { // ?????? ???

                    NMU_EDIT_COST_USER_CNT += 1;

                    if (NMU_EDIT_COST_USER_CNT + MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT >= moimingCnt) {
                        Toast.makeText(context.getApplicationContext(), "?????? ????????? ???????????????", Toast.LENGTH_SHORT).show();

                        NMU_EDIT_COST_USER_CNT -= 1;

                        cb.setChecked(false);
                    } else {

                        holder.userCost.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_edit_personal_cost, null));
                        holder.userCost.setEnabled(true);
                    }

                } else { // ?????? ???

                    NMU_EDIT_COST_USER_CNT -= 1;

                    holder.userCost.setBackgroundColor(Color.TRANSPARENT);
                    holder.userCost.setText(AppExtraMethods.moneyToWonWon(nmuDatas.getUserCost()));
                    holder.userCost.setEnabled(false);
                    int prevFundingCost = nmuDatas.getUserCost();

                    // ?????? ?????? ???????????? ??????????????? N????????? ??????????????? ???.
                    if (nmuDatas.getIsEdited()) {

                        cancelCheckBoxListener.onCancelCheckbox(false, position, prevFundingCost);
                    }

                }

            }
        });

        if (nmuDatas.getIsEdited()) {

            holder.isEdited.setChecked(true);
            holder.userCost.setEnabled(true);
            holder.userCost.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_edit_personal_cost, null));

        } else {

            holder.isEdited.setChecked(false);
            holder.userCost.setEnabled(false);
            holder.userCost.setBackgroundColor(Color.TRANSPARENT);

        }

        if (nmuDatas.isAutoChanged()) {

            holder.isAutoChanged.setVisibility(View.VISIBLE);
            nmuDatas.setAutoChanged(false);

        } else {
            holder.isAutoChanged.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return nmuList.size();
    }

    public void setMoimingCnt(int moimingCnt) {

        this.moimingCnt = moimingCnt;


    }

    class NmuEditViewHolder extends RecyclerView.ViewHolder {

        private EditText userName, userCost;
        private CheckBox isEdited;
        private View isAutoChanged;

        public NmuEditViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.text_personal_name);
            userCost = itemView.findViewById(R.id.input_personal_cost);
            userCost.setEnabled(false); // ?????? ??????

            isEdited = itemView.findViewById(R.id.is_cost_edited);
            isAutoChanged = itemView.findViewById(R.id.is_auto_changed);

        }
    }
}
