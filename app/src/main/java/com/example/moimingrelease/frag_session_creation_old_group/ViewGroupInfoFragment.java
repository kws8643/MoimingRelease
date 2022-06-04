package com.example.moimingrelease.frag_session_creation_old_group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moimingrelease.GroupActivity;
import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionCreationActivity;
import com.example.moimingrelease.SessionCreationInOldGroupActivity;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.view.GroupInfoMemberViewer;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupRetrofitService;
import com.example.moimingrelease.network.TransferModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ViewGroupInfoFragment extends Fragment {

    private SessionCreationInOldGroupActivity activity;

    private MoimingGroupVO selectedGroup;
    private MoimingUserVO curUser;

    private ImageView imgGroupPf, btnFinish;
    private TextView textGroupName, textGroupInfo, textMemberCnt;
    private GridLayout layoutMembersGrid;
    private Button btnChooseGroup;

    private List<MoimingMembersDTO> membersList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            curUser = (MoimingUserVO) getArguments().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            selectedGroup = (MoimingGroupVO) getArguments().getSerializable(getResources().getString(R.string.moiming_group_data_key));
            membersList = (List<MoimingMembersDTO>) getArguments().getSerializable(getResources().getString(R.string.group_members_data_key));

            for(int i = 0; i < membersList.size(); i++){
                if(((MoimingMembersDTO)membersList.get(i)).getUuid().equals(curUser.getUuid())){
                    membersList.remove(i);
                    i--;
                }
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_session_creation_old_view_group, container, false);

        initView(view);

        initParams();

        fillGridLayout();

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        btnChooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent createDutchpay = new Intent(activity.getApplicationContext(), SessionCreationActivity.class);

                createDutchpay.putExtra(activity.getResources().getString(R.string.session_creation_type_key), 1);
                createDutchpay.putExtra(activity.getResources().getString(R.string.moiming_user_data_key), curUser);
                createDutchpay.putExtra(activity.getResources().getString(R.string.moiming_group_data_key), (Serializable) selectedGroup);
                createDutchpay.putParcelableArrayListExtra(GroupActivity.MOIMING_GROUP_MEMBERS_KEY, (ArrayList<MoimingMembersDTO>) membersList);
//                createDutchpay.putExtra(activity.getResources().getString(R.string.session_creation_from_group_activity_flag), false);
                createDutchpay.putExtra(activity.getResources().getString(R.string.session_creation_with_new_group_flag), false);
                createDutchpay.putExtra("from_main_activity", true);

                activity.startActivity(createDutchpay);

                activity.finish();
            }
        });


        return view;

    }

    private void initView(View view) {

        imgGroupPf = view.findViewById(R.id.img_group_info_pf);
        imgGroupPf.setClipToOutline(true);

        textGroupName = view.findViewById(R.id.text_group_info_name);
        textGroupName.setText(selectedGroup.getGroupName());

        textGroupInfo = view.findViewById(R.id.text_group_info_info);
        textGroupInfo.setText(selectedGroup.getGroupInfo());

        textMemberCnt = view.findViewById(R.id.text_group_info_member_cnt);
        textMemberCnt.setText(String.valueOf(selectedGroup.getGroupMemberCnt()) + " 명");

        btnChooseGroup = view.findViewById(R.id.btn_choose_group);

        layoutMembersGrid = view.findViewById(R.id.layout_grid_members);

        btnFinish = view.findViewById(R.id.btn_back_view_group);
    }

    private void initParams() {

        activity = (SessionCreationInOldGroupActivity) getActivity();

    }


    private void fillGridLayout() {

        // 1) 나를 넣는다.
        GroupInfoMemberViewer curUserViewer = new GroupInfoMemberViewer(activity.getApplicationContext());
        curUserViewer.setTextUserName(curUser.getUserName());
        curUserViewer.setImgUserPfImg(curUser.getUserPfImg());

        layoutMembersGrid.addView(curUserViewer);
        giveMarginInScrollView(curUserViewer);

        // 2) 다른 친구들을 넣는다.
        for (int i = 0; i < membersList.size(); i++) {

            MoimingMembersDTO members = membersList.get(i);

            GroupInfoMemberViewer memberViewer = new GroupInfoMemberViewer(activity.getApplicationContext());
            memberViewer.setTextUserName(members.getUserName());
            memberViewer.setImgUserPfImg(members.getUserPfImg());

            layoutMembersGrid.addView(memberViewer);
            giveMarginInScrollView(memberViewer);

        }

    }

    private void giveMarginInScrollView(GroupInfoMemberViewer viewer) {

        GridLayout.LayoutParams params = (GridLayout.LayoutParams) viewer.getLayoutParams();

        params.setMargins(0, 0, dpToPx(24), dpToPx(30));

        viewer.setLayoutParams(params);

    }


    // dp -> pixel 단위로 변경
    private int dpToPx(int dp) {

        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


}
