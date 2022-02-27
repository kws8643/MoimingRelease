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

                // 리스너 실행 --> Main 치는 값들이 전달되게끔.
                String curNmuUserName = holder.userName.getText().toString();
                nmuNameChangeListener.saveNmuName(position, curNmuUserName);
            }
        });

        int userCharge = nmuDatas.getUserCost();
        holder.userCost.setText(String.valueOf(userCharge));


        holder.userCost.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

                String personalCostStr = view.getText().toString();

                if (personalCostStr.isEmpty()) {

                    Toast.makeText(view.getContext(), "해당 유저의 금액을 입력해주세요", Toast.LENGTH_SHORT).show();

                } else if (userCharge == Integer.parseInt(personalCostStr)) { //  동일한 금액을 입력했다고 알린다.

                    Toast.makeText(view.getContext(), "동일한 금액입니다", Toast.LENGTH_SHORT).show();

                } else {

                    int personalCost = Integer.parseInt(personalCostStr); // 바꾸려는 사람 금액.
                    // 누구를 얼마로 변경했는지 밖으로 전달한다.

                    editCostListener.onFinishEditing(false, position, personalCost, userCharge);
                }

                /*NMU_EDIT_COST_USER_CNT = 0;

                for (int i = 0; i < nmuList.size(); i++) {

                    SessionNmuLinkerData data = nmuList.get(i);
                    if (data.getIsEdited()) NMU_EDIT_COST_USER_CNT++;

                }*/

                setNmuEditCostUserCnt();

                cntListener.editUserCnt(false);

               /* Toast.makeText(context.getApplicationContext(), NMU_EDIT_COST_USER_CNT + ", "
                        + MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT + ", "
                        + String.valueOf(NMU_EDIT_COST_USER_CNT + MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT), Toast.LENGTH_SHORT).show();

*/
                return false;
            }
        });

        holder.isEdited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) { // 선택 시

                    NMU_EDIT_COST_USER_CNT += 1;

                    if (NMU_EDIT_COST_USER_CNT + MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT >= moimingCnt) {
                        Toast.makeText(context.getApplicationContext(), "전원 수정은 불가합니다", Toast.LENGTH_SHORT).show();

                        NMU_EDIT_COST_USER_CNT -= 1;

                        cb.setChecked(false);
                    } else {

                        holder.userCost.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_edit_personal_cost, null));
                        holder.userCost.setEnabled(true);
                    }

                    /*Toast.makeText(context.getApplicationContext(), NMU_EDIT_COST_USER_CNT + ", "
                            + MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT + ", "
                            + String.valueOf(NMU_EDIT_COST_USER_CNT + MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT), Toast.LENGTH_SHORT).show();
*/
                } else { // 해제 시

//                    SessionCreationActivity.COST_EDIT_USER_CNT -= 1;
                    NMU_EDIT_COST_USER_CNT -= 1;

                    holder.userCost.setBackgroundColor(Color.TRANSPARENT);
                    holder.userCost.setText(String.valueOf(nmuDatas.getUserCost()));
                    holder.userCost.setEnabled(false);
                    int prevFundingCost = nmuDatas.getUserCost();

                    // 누른 애가 해제되는 시점에서의 N빵으로 계산시키면 됨.
                    if (nmuDatas.getIsEdited()) {

                        cancelCheckBoxListener.onCancelCheckbox(false, position, prevFundingCost);
                    }

                    Toast.makeText(context.getApplicationContext(), NMU_EDIT_COST_USER_CNT + ", "
                            + MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT + ", "
                            + String.valueOf(NMU_EDIT_COST_USER_CNT + MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT), Toast.LENGTH_SHORT).show();

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
            userCost.setEnabled(false); // 수정 금지

            isEdited = itemView.findViewById(R.id.is_cost_edited);
            isAutoChanged = itemView.findViewById(R.id.is_auto_changed);

        }
    }
}
