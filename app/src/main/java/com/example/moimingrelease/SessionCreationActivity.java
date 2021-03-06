package com.example.moimingrelease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_adapter.AppExtraMethods;
import com.example.moimingrelease.app_adapter.MembersEditPersonalCostRecyclerAdapter;
import com.example.moimingrelease.app_adapter.NmuEditPersonalCostRecyclerAdapter;
import com.example.moimingrelease.app_adapter.RecyclerViewItemDecoration;
import com.example.moimingrelease.app_listener_interface.CancelActivityCallBack;
import com.example.moimingrelease.app_listener_interface.RegisterAccountListener;
import com.example.moimingrelease.app_listener_interface.SessionCreationNmuNameChangeListener;
import com.example.moimingrelease.app_listener_interface.SessionPersonalCostEditCntListener;
import com.example.moimingrelease.app_listener_interface.UserCostCancelCheckBoxListener;
import com.example.moimingrelease.app_listener_interface.UserEditPersonalCostListener;
import com.example.moimingrelease.moiming_model.dialog.CancelActivityDialog;
import com.example.moimingrelease.moiming_model.dialog.RegisterAccountDialog;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.UserSessionLinkerVO;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionMemberLinkerData;
import com.example.moimingrelease.moiming_model.recycler_view_model.SessionNmuLinkerData;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.request_dto.GroupAndSessionCreationDTO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingGroupRequestDTO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingSessionRequestDTO;
import com.example.moimingrelease.moiming_model.request_dto.USLinkerRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingSessionResponseDTO;
import com.example.moimingrelease.moiming_model.view.CustomUserViewer;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupRetrofitService;
import com.example.moimingrelease.network.SessionRetrofitService;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.USLinkerRetrofitService;
import com.example.moimingrelease.network.kakao_api.KakaoMoimingFriends;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// TODO ???????????? ??????.
public class SessionCreationActivity extends AppCompatActivity {

    public final String SESSION_CREATION_TAG = "SESSION_CREATION_TAG";
    public static final String INVITED_MEMBER_DATA_KEY = "INVITED_MEMBERS";
    public static final String INVITED_NMU_CNT = "INVITED_NMU_CNT";
    public static int COST_EDIT_USER_CNT = 0;

    private Integer sessionType;

    private InputMethodManager imm;

    // App Bar
    private TextView textSessionType;

    // NMU
    private ConstraintLayout nmuInviteViewer;
    private TextView textNmuCnt;

    private TextView btnCreationFinish, btnPrevFunding;
    private ConstraintLayout layoutSetMembers, layoutSessionResult;

    private TextView btnChooseMembers, btnEditSessionName, btnEditCost, textCostType;
    private TextView textInviteCnt;
    private LinearLayout layoutUsersHolder;
    private EditText inputSessionName, inputCost;
    private NestedScrollView scroll;

    private Map<Integer, Boolean> isReady;
    public boolean isPersonalEditOpen = false;
    private boolean fromNewGroupCreation;
    MoimingUserVO curUser;
    MoimingGroupVO curGroup;
    List<MoimingMembersDTO> curGroupMembers;
    List<MoimingMembersDTO> sessionInvitedMembers;
    public int inviteNmuCnt = 0;
    public int totalCost = 0;
    public int fundingCost = 0;

    private List<CustomUserViewer> addedViewerList; // ????????? ??????????????? ??????

    private String inputCostForEditText = "";


    private TextWatcher moneyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            if (!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(inputCostForEditText)) {

                inputCostForEditText = AppExtraMethods.wonFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                inputCost.setText(inputCostForEditText);
                inputCost.setSelection(inputCostForEditText.length());
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private SessionCreationNmuNameChangeListener nmuNameChangeListener = new SessionCreationNmuNameChangeListener() { //????????? ??? ????????? ??????
        @Override
        public void saveNmuName(int position, String userName) {

            SessionNmuLinkerData data = nmuSessionDataList.get(position);
            data.setUserName(userName);

            nmuSessionDataList.set(position, data);

        }
    };

    private SessionPersonalCostEditCntListener cntListener = new SessionPersonalCostEditCntListener() {
        @Override
        public void editUserCnt(boolean isMoimingUser) {

            if (isMoimingUser) { // Nmu Cnt ??????

                nmuAdapter.setNmuEditCostUserCnt();

            } else { // Moiming Cnt ??????

                membersAdapter.setMoimingEditCostUserCnt();

            }

        }
    };

    TextView.OnEditorActionListener inputListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            // ??? ?????? ????????? ??????. v.get ID ??? ?????????!
            String inputData = v.getText().toString();
            int id = v.getId();

            switch (id) {
                case R.id.input_session_name:

                    if (inputSessionName.getText().toString().isEmpty()) {

                        Toast.makeText(getApplicationContext(), "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();

                    } else { // ?????????.

//                        if (!isReady.get(0)) {
                        //1. EDIT TEXT ?????? ????????????
                        inputSessionName.setEnabled(false);
                        btnEditSessionName.setVisibility(View.VISIBLE);

                        if (!isReady.get(0)) {
                            inputCost.setEnabled(true);
                            inputCost.requestFocus();
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                            //2. ?????? ?????????
                            TextView text2 = findViewById(R.id.text_2);
                            text2.setBackground(getResources().getDrawable(R.drawable.shape_round_bg_lightpurple, null));
//                        text2.setTextColor(getResources().getColor(R.color.moimingWhite, null));

                            isReady.put(0, true);
                        }
//
                    }
                    break;

                case R.id.input_cost:

                    layoutSetMembers.performClick(); // ?????? ????????? ?????? ?????? ????????? ?????????.

                    break;
            }
            return false;
        }
    };


    private CancelActivityCallBack finishCallBack = new CancelActivityCallBack() {
        @Override
        public void finishActivity() {

            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_session_creation);

        receiveIntent();

        initView();

        initParams();

        initHorizontalView();

        COST_EDIT_USER_CNT = 0;

        inputCost.addTextChangedListener(moneyWatcher);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dialog
                CancelActivityDialog dialog = new CancelActivityDialog(SessionCreationActivity.this, finishCallBack, "???????????? ????????? ???????????????");

                dialog.show();
            }
        });

        btnChooseMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isReady.get(0) && isReady.get(1)) {

                    Intent toChooseMember = new Intent(SessionCreationActivity.this, InviteSessionMembersInGroupActivity.class);

                    if (!fromNewGroupCreation) {

                        toChooseMember.putExtra(MainActivity.MOIMING_USER_DATA_KEY, curUser);
                        toChooseMember.putExtra(INVITED_NMU_CNT, inviteNmuCnt);
                        toChooseMember.putParcelableArrayListExtra(GroupActivity.MOIMING_GROUP_MEMBERS_KEY, (ArrayList<MoimingMembersDTO>) curGroupMembers);
                        toChooseMember.putParcelableArrayListExtra(INVITED_MEMBER_DATA_KEY, (ArrayList<MoimingMembersDTO>) sessionInvitedMembers);

                    } else {

                        toChooseMember.putExtra(MainActivity.MOIMING_USER_DATA_KEY, curUser);
                        toChooseMember.putExtra(getResources().getString(R.string.session_creation_with_new_group_flag), true);
                        toChooseMember.putExtra(INVITED_NMU_CNT, inviteNmuCnt);
                        toChooseMember.putParcelableArrayListExtra(INVITED_MEMBER_DATA_KEY, (ArrayList<MoimingMembersDTO>) sessionInvitedMembers);


                    }

                    startActivityForResult(toChooseMember, 100);

                } else {

                    Toast.makeText(getApplicationContext(), "?????? ???????????? ?????? ?????? ??? ???????????? ?????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnEditSessionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnEditSessionName.setVisibility(View.GONE);

                inputSessionName.setEnabled(true);
                inputSessionName.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });


        btnEditCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnEditCost.setVisibility(View.GONE);
                textCostType.setVisibility(View.GONE);
                inputCost.setEnabled(true);
                inputCost.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });


        inputCost.setOnTouchListener(new View.OnTouchListener() { // ????????? ??????????????? ??????
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {

                    if (inputSessionName.getText().toString().isEmpty()) {

                        Toast.makeText(getApplicationContext(), "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();

                    } else { // ?????????.

                        if (!isReady.get(0)) {
                            //1. EDIT TEXT ?????? ????????????
                            inputCost.setEnabled(true);
                            inputCost.requestFocus();
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                            inputSessionName.setEnabled(false);
                            btnEditSessionName.setVisibility(View.VISIBLE);

                            //2. ?????? ?????????
                            TextView text2 = findViewById(R.id.text_2);
                            text2.setBackground(getResources().getDrawable(R.drawable.shape_round_bg_lightpurple, null));
//                        text2.setTextColor(getResources().getColor(R.color.moimingWhite, null));

                            isReady.put(0, true);
                        }
                    }
                }

                return false;
            }
        });


        layoutSetMembers.setOnClickListener(new View.OnClickListener() { // Cost ??? ??????????????? ??????.
            @Override
            public void onClick(View v) {

                // ????????? ????????? ???????????? ???
                String inputData = AppExtraMethods.wonToNormal(inputCost.getText().toString());

                if (inputData.isEmpty() || Integer.parseInt(inputData) == 0) {

                    Toast.makeText(getApplicationContext(), "?????? ????????? ??????????????????", Toast.LENGTH_SHORT).show();

                } else {

                    // ????????? ?????????
                    if (!isReady.get(1)) { // ?????? ??????????????? ??????

                        if (sessionType == 1) { // ??????????????? ??????

                            totalCost = Integer.parseInt(inputData); // ????????? ??????

                        } else { // ????????? ??????

                            fundingCost = Integer.parseInt(inputData);
                        }

                        // 1. EDIT TEXT ?????????
                        inputCost.setEnabled(false);
                        btnEditCost.setVisibility(View.VISIBLE);
                        textCostType.setVisibility(View.VISIBLE);

                        // ?????? ??? ????????????
                        TextView text3 = findViewById(R.id.text_3);
                        text3.setBackground(getResources().getDrawable(R.drawable.shape_round_bg_lightpurple, null));
                        TextView text3_1 = findViewById(R.id.text_3_1);
                        text3_1.setTextColor(getResources().getColor(R.color.textDark, null));

                        isReady.put(1, true);

                    } else { // ????????? ???????????? ??????

                        if (sessionType == 1) { // ??????????????? ??????

                            totalCost = Integer.parseInt(inputData); // ????????? ??????

                        } else { // ????????? ??????

                            fundingCost = Integer.parseInt(inputData); // ?????? ?????? (?????? ?????? ??????)
                        }

                        inputCost.setEnabled(false);
                        btnEditCost.setVisibility(View.VISIBLE);
                        textCostType.setVisibility(View.VISIBLE);

                        if (isReady.get(2)) { // MEMBER?????? ??? ??? ????????? ???????????? ????????? ????????? ??????
//                            setReceipt();
                            refreshAllSessionByTotalCost();
                            findViewById(R.id.view_edited_cost).setVisibility(View.GONE);

                        }
                    }
                }
            }
        });


        layoutPersonalCost.setOnClickListener(new View.OnClickListener() { // ????????? ?????? ???????????? ????????????.
            @Override
            public void onClick(View v) {

                if (!isReady.get(2)) {

                    Toast.makeText(v.getContext(), "????????? ????????? ?????? ?????? ??? ???????????? ?????????", Toast.LENGTH_SHORT).show();

                } else {


                    if (!isPersonalEditOpen) { // ?????? ???????????? ??????

                        scrollToView(layoutPersonalCost, scroll, 0);

                        TextView text4_1 = findViewById(R.id.text_4_1);
                        text4_1.setTextColor(getResources().getColor(R.color.textDark, null));
                        findViewById(R.id.view_4).setBackground(getResources().getDrawable(R.drawable.shape_round_bg_lightpurple, null));


                        // ????????? Edit Text ????????? ??????
                        layoutPersonalCost.setFocusable(true);
                        layoutPersonalCost.setFocusableInTouchMode(true);

                        isPersonalEditOpen = true;

                        //???????????? ??? ??????
                        layoutDrawer.setVisibility(View.VISIBLE);
                        sizeMatcher.setVisibility(View.GONE);
                        btnResetPersonal.setVisibility(View.VISIBLE);
                    }

                }
            }
        });


        // ???????????? ?????? (?????? ?????? ????????? ??????)
        btnResetPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setTitle("????????? ?????? ?????????").setMessage("?????? ????????? ?????? 1/N ?????????. ?????????????????????????")
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT = 0;
                                NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT = 0;

                                int invitedCnt = sessionInvitedMembers.size() + inviteNmuCnt + 1;

                                if (sessionType == 1) {

                                    totalCost = Integer.parseInt(AppExtraMethods.wonToNormal(inputCost.getText().toString()));

                                    int dividedN = (int) (totalCost / invitedCnt);
                                    int curUserCost = dividedN;

                                    if ((dividedN * invitedCnt) != totalCost) {

                                        int diff = totalCost - (dividedN * invitedCnt);
                                        curUserCost += diff;
                                    }

                                    for (int i = 0; i < membersSessionDataList.size(); i++) {

                                        SessionMemberLinkerData linkerData = membersSessionDataList.get(i);

                                        if (i == 0) {
                                            linkerData.setUserCost(curUserCost);
                                            linkerData.setIsEdited(false);
                                        } else {

                                            linkerData.setUserCost(dividedN);
                                            linkerData.setIsEdited(false);
                                        }

                                        membersSessionDataList.set(i, linkerData);
                                    }

                                    for (int i = 0; i < inviteNmuCnt; i++) {

                                        SessionNmuLinkerData nmuLinkerData = nmuSessionDataList.get(i);

                                        nmuLinkerData.setUserCost(dividedN);
                                        nmuLinkerData.setIsEdited(false);

                                        nmuSessionDataList.set(i, nmuLinkerData);
                                    }

                                    // ????????? ??????
                                    resultMemberCnt.setText(String.valueOf(invitedCnt));
                                    resultPersonalCost.setText(AppExtraMethods.moneyToWonWon(dividedN));
                                    resultTotalCost.setText(AppExtraMethods.moneyToWonWon(totalCost));

                                } else { // ????????? ??????


                                    int personalFundingCost = Integer.parseInt(AppExtraMethods.wonToNormal(inputCost.getText().toString()));

                                    totalCost = personalFundingCost * invitedCnt;

                                    for (int i = 0; i < membersSessionDataList.size(); i++) {

                                        SessionMemberLinkerData linkerData = membersSessionDataList.get(i);

                                        linkerData.setUserCost(personalFundingCost);
                                        linkerData.setIsEdited(false);

                                        membersSessionDataList.set(i, linkerData);
                                    }

                                    for (int i = 0; i < inviteNmuCnt; i++) {

                                        SessionNmuLinkerData nmuLinkerData = nmuSessionDataList.get(i);

                                        nmuLinkerData.setUserCost(personalFundingCost);
                                        nmuLinkerData.setIsEdited(false);

                                        nmuSessionDataList.set(i, nmuLinkerData);
                                    }

                                    resultMemberCnt.setText(String.valueOf(invitedCnt));
                                    resultPersonalCost.setText(AppExtraMethods.moneyToWonWon(personalFundingCost));
                                    resultTotalCost.setText(AppExtraMethods.moneyToWonWon(totalCost));
                                }

                                membersAdapter.notifyDataSetChanged();
                                nmuAdapter.notifyDataSetChanged();

                                findViewById(R.id.view_edited_cost).setVisibility(View.GONE);


                                dialog.dismiss();
                                dialog = null;
                            }
                        })
                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                dialog.dismiss();
                                dialog = null;
                            }
                        });

                dialog = builder.create();
                dialog.show();

            }
        });


        btnCreationFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isReady.get(0) && isReady.get(1) && isReady.get(2)) {

                    if (!fromNewGroupCreation) { // ???????????? ??????

                        requestSessionBuild();

                    } else {

                        //TODO ??? ???????????? ???????????? ????????? ?????? ?????? ???????????? ??? ???????????? ???
                        createNewGroupAndSession();

                    }

                } else {

                    Toast.makeText(v.getContext(), "?????? ???????????? ???????????? ?????????", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnPrevFunding.setOnClickListener(new View.OnClickListener() { // Group ????????? ??????.
            @Override
            public void onClick(View v) {

                Intent prevFundingIntent = new Intent(SessionCreationActivity.this, SessionCreationPreviousFundingActivity.class);

                // user, group ?????? ??????
                prevFundingIntent.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                prevFundingIntent.putExtra(getResources().getString(R.string.moiming_group_data_key), (Parcelable) curGroup);

                startActivityForResult(prevFundingIntent, 200);

            }
        });

    }

    // TODO: ?????? ???????????? ???, ?????? > ?????? ?????? > ?????? > ?????? ?????? ????????? ???????????????

    private MoimingGroupAndMembersDTO response;

    private void createNewGroupAndSession() { // ???????????? ?????? ????????? ???, ?????? ???????????? ????????? ?????????.
//    public MoimingGroupRequestDTO(String groupName, String groupInfo, UUID groupCreatorUuid, String bgImg, Integer groupMemberCnt) {

        String groupName = inputSessionName.getText().toString();

        int moimingMemberCnt = membersSessionDataList.size();
        // ??? ????????? ?????????.
        List<UUID> membersUuidList = new ArrayList<>();

        for (SessionMemberLinkerData memberData : membersSessionDataList) { // ????????? ????????? ???????????? ??????.
            UUID memberUuid = memberData.getUuid();
            membersUuidList.add(memberUuid);
        }
        // 1. ????????? ?????????.
        MoimingGroupRequestDTO groupRequest = new MoimingGroupRequestDTO(groupName, null, curUser.getUuid(), null, moimingMemberCnt);

        // 2. ?????? ????????? ????????? ???????????? ?????????.
        // 3. ??????????????? ????????? ?????????.
        MoimingSessionRequestDTO sessionRequest = makeSessionRequestData();

        // 4. ???????????? ?????? ????????? ?????? ?????????.
        List<USLinkerRequestDTO> usDataList = new ArrayList<>();

        // 4-1) Moiming User ?????? ??????
        for (int i = 0; i < membersSessionDataList.size(); i++) {

            SessionMemberLinkerData memberData = membersSessionDataList.get(i);

            USLinkerRequestDTO requestDTO = new USLinkerRequestDTO(null, memberData.getUuid(), true, memberData.getUserName(), memberData.getUserCost());

            usDataList.add(requestDTO);
        }

        // 2-2) NMU ?????? ??????
        for (int i = 0; i < nmuSessionDataList.size(); i++) {

            SessionNmuLinkerData nmuData = nmuSessionDataList.get(i);

            USLinkerRequestDTO nmuRequestDTO = new USLinkerRequestDTO(null, false, nmuData.getUserName(), nmuData.getUserCost());

            usDataList.add(nmuRequestDTO);

        }

        // TODO: ?????? > ?????? ?????? > ?????? > ?????? ?????? ????????? ??????????????? ?????? ???????????? ?????????.
        GroupAndSessionCreationDTO groupSessionRequest = new GroupAndSessionCreationDTO(groupRequest, sessionRequest, membersUuidList, usDataList);
        TransferModel<GroupAndSessionCreationDTO> requestModel = new TransferModel<>(groupSessionRequest);

        GroupRetrofitService groupRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupRetrofitService.class);
        groupRetrofit.groupCreationWithSessionRequest(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<MoimingGroupAndMembersDTO>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<MoimingGroupAndMembersDTO> responseModel) {

                        response = responseModel.getData();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                        MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = true;

                        Intent groupIntent = new Intent(SessionCreationActivity.this, GroupActivity.class);

                        groupIntent.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                        groupIntent.putExtra(getResources().getString(R.string.moiming_group_and_members_data_key), (Parcelable) response);
                        groupIntent.putExtra("created_with_session", true);
//                        groupIntent.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) response.getMoimingGroupDto().convertToVO());

                        startActivity(groupIntent);

                        finish();
                    }
                });

                /*.subscribe(new Observer<TransferModel<MoimingGroupResponseDTO>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<MoimingGroupResponseDTO> responseModel) {

                        createdGroup = responseModel.getData().convertToVO();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {
                            Log.e(SESSION_CREATION_TAG, e.getMessage());
                        }

                    }

                    @Override
                    public void onComplete() {

                        // ??????????????? ?????? ??? Group Activity ??? ???????????????.
                        MoimingGroupAndMembersDTO thisDTo = null;


                        Intent groupIntent = new Intent(SessionCreationActivity.this, GroupActivity.class);

                        groupIntent.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                        groupIntent.putExtra(getResources().getString(R.string.moiming_group_and_members_data_key), thisDTo);

                        groupIntent.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) createdGroup);

                        startActivity(groupIntent);

                        finish();
                    }
                });*/


    }


    // ??? ????????? ?????? ????????? ????????? ??????
    private void refreshAllSessionByTotalCost() {

       /* if (sessionType == 1) { // ??????????????? ??????

            totalCost = Integer.parseInt(inputData); // ????????? ??????

        } else { // ????????? ??????

            fundingCost = Integer.parseInt(inputData); // ?????? ?????? (?????? ?????? ??????)
        }

        inputCost.setEnabled(false);
        btnEditCost.setVisibility(View.VISIBLE);
        textCostType.setVisibility(View.VISIBLE);*/


        int newDividedN;
        int invitedCnt = sessionInvitedMembers.size() + inviteNmuCnt + 1;

        if (sessionType == 1) { // ??????????????? ??????

            newDividedN = (int) (totalCost / invitedCnt);
            int curUserCost = newDividedN;

            if ((newDividedN * invitedCnt) != totalCost) {

                int diff = totalCost - (newDividedN * invitedCnt);
                curUserCost += diff;
            }

            for (int i = 0; i < membersSessionDataList.size(); i++) {

                SessionMemberLinkerData linkerData = membersSessionDataList.get(i);
                if (i == 0) {
                    linkerData.setUserCost(curUserCost);
                    linkerData.setIsEdited(false);
                } else {

                    linkerData.setUserCost(newDividedN);
                    linkerData.setIsEdited(false);
                }

                membersSessionDataList.set(i, linkerData);
            }

            for (int i = 0; i < inviteNmuCnt; i++) {

                SessionNmuLinkerData nmuLinkerData = nmuSessionDataList.get(i);

                nmuLinkerData.setUserCost(newDividedN);
                nmuLinkerData.setIsEdited(false);

                nmuSessionDataList.set(i, nmuLinkerData);
            }

            editReceiptDutchpay(true, newDividedN);

        } else { // ????????? ??????


            for (int i = 0; i < membersSessionDataList.size(); i++) {

                SessionMemberLinkerData linkerData = membersSessionDataList.get(i);
                linkerData.setUserCost(fundingCost);
                linkerData.setIsEdited(false);

                membersSessionDataList.set(i, linkerData);
            }

            for (int i = 0; i < inviteNmuCnt; i++) {

                SessionNmuLinkerData nmuLinkerData = nmuSessionDataList.get(i);

                nmuLinkerData.setUserCost(fundingCost);
                nmuLinkerData.setIsEdited(false);

                nmuSessionDataList.set(i, nmuLinkerData);
            }

            totalCost = invitedCnt * fundingCost;
            refreshReceiptFundings();
        }

        membersAdapter.notifyDataSetChanged();
        nmuAdapter.notifyDataSetChanged();

    }

    boolean fromMainActivity;

    private void receiveIntent() {

        Intent dataIntent = getIntent();

        sessionType = dataIntent.getExtras().getInt(getResources().getString(R.string.session_creation_type_key));
        curGroup = (MoimingGroupVO) dataIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));
        curUser = (MoimingUserVO) dataIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        curGroupMembers = dataIntent.getParcelableArrayListExtra(GroupActivity.MOIMING_GROUP_MEMBERS_KEY);
        fromNewGroupCreation = dataIntent.getExtras().getBoolean(getResources().getString(R.string.session_creation_with_new_group_flag), false);
        fromMainActivity = dataIntent.getExtras().getBoolean("from_main_activity", false);

        if (curUser.getBankName() == null || curUser.getBankNumber() == null) {

            showRegisterAccountDialog();
        }

        // ???????????? ?????? ???????????? ??????.
        // ??????????????? ?????? ???.
        // 1. ????????? ????????????
        // 2. GroupMembers ?????? ????????? ?????????????????? ??????. <???????????? ??????>
        // 3. ????????? Group ?????? --> Session ?????? --> SessionLinker ??????
        // ????????? ?????? ??????.

        if (fromNewGroupCreation) { // ?????? ????????? ??????
            sessionType = 1;
            curGroup = null;
            curGroupMembers = null;

            KakaoMoimingFriends kmf = KakaoMoimingFriends.getInstance(); // ????????? ???????????? ?????? ?????? ???????????????. // List ??????

        }
    }

    private RegisterAccountListener registerAccountListener = new RegisterAccountListener() {
        @Override
        public void cancelOrConfirm(boolean isConfirmed) {

            if (isConfirmed) {

                Intent registerAccountIntent = new Intent(SessionCreationActivity.this, RegisterBankAccountActivity.class);

                registerAccountIntent.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);

                startActivityForResult(registerAccountIntent, 10);


            } else {

                Toast.makeText(getApplicationContext(), "????????? ????????? ????????? ?????? ?????? ????????? ???????????????", Toast.LENGTH_SHORT).show();

                finish();

            }
        }
    };

    private void showRegisterAccountDialog() {

        RegisterAccountDialog registerDialog = new RegisterAccountDialog(this, registerAccountListener);

        registerDialog.show();

    }

    private ImageView btnBack;

    private void initView() {

        btnBack = findViewById(R.id.btn_back_session_creation);

        //NMU
        nmuInviteViewer = findViewById(R.id.viewer_non_moiming_member_out);
        textNmuCnt = findViewById(R.id.text_nmu_cnt_out);
        textNmuRef = findViewById(R.id.b);

        // ????????? ???
        layoutSessionResult = findViewById(R.id.layout_session_result);
        resultPersonalCost = findViewById(R.id.text_result_personal_cost);
        resultTotalCost = findViewById(R.id.text_result_cost);
        resultMemberCnt = findViewById(R.id.text_result_member);
        // Activity

        // Title
        textSessionType = findViewById(R.id.text_session_type);
        btnPrevFunding = findViewById(R.id.btn_callback_fundings);

        btnChooseMembers = findViewById(R.id.btn_choose_members);
        btnEditCost = findViewById(R.id.btn_edit_cost);
        textCostType = findViewById(R.id.text_cost_type_indicator);
        btnEditSessionName = findViewById(R.id.btn_edit_session_name);
        btnCreationFinish = findViewById(R.id.btn_finish_create_session);

        layoutUsersHolder = findViewById(R.id.layout_users_holder_out);
        layoutPersonalCost = findViewById(R.id.layout_4);

        textInviteCnt = findViewById(R.id.text_invite_cnt_out); //  HZV ?????? ?????? CNT

        inputCost = findViewById(R.id.input_cost);
        inputCost.setOnEditorActionListener(inputListener);

        inputSessionName = findViewById(R.id.input_session_name);
        inputSessionName.setOnEditorActionListener(inputListener);

        layoutSetMembers = findViewById(R.id.layout_3);

        scroll = findViewById(R.id.session_creation_scroll);


        // ????????? ?????? ?????? ???.
        layoutDrawer = findViewById(R.id.layout_drawer);
        textMemberInviteCnt = findViewById(R.id.text_member_invite_cnt);
        textNmuInviteCnt = findViewById(R.id.text_nmu_invite_cnt);
        memberRecycler = findViewById(R.id.invite_session_member_recycler);
        nmuRecycler = findViewById(R.id.invite_session_nmu_recycler);
        sizeMatcher = findViewById(R.id.size_matcher);
        btnResetPersonal = findViewById(R.id.btn_reset);

        // ????????? ??????
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        if (sessionType == 0) { // ??????

            // 1. ?????? text
            textSessionType.setText("??? ?????? ??????");

            // 2. ?????? text
            inputSessionName.setHint("?????? ?????? ??????");
            inputCost.setHint("?????? ?????? ?????? ??????");
            textCostType.setText("?????? ?????? (???)");

            // 3. ?????? ?????? ?????? ??????
            btnPrevFunding.setVisibility(View.VISIBLE);


        } else { // ????????????

            // 1. ?????? text
            textSessionType.setText("??? ???????????? ??????");

            // 2. ?????? text
            inputSessionName.setHint("???????????? ?????? ??????");
            inputCost.setHint("???????????? ??? ?????? ??????");
            textCostType.setText("??? ?????? (???)");

            // 3. ?????? ?????? ?????? ??????
            btnPrevFunding.setVisibility(View.GONE);

        }

    }


    private void initParams() {

        sessionInvitedMembers = new ArrayList<>();
        addedViewerList = new ArrayList<>();

        membersSessionDataList = new ArrayList<>();
        nmuSessionDataList = new ArrayList<>();

        isReady = new HashMap<>();
        isReady.put(0, false);
        isReady.put(1, false);
        isReady.put(2, false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) { // ?????? ??????! RegisterBankAccount???

            if (resultCode == RESULT_OK) {

                // ?????? ???????????? ??? ???????????? ??????!
                curUser = (MoimingUserVO) data.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));

                Toast.makeText(getApplicationContext(), "????????? ??????????????? ?????????????????????. ?????? ????????? ??????????????????!", Toast.LENGTH_SHORT).show();


            } else if (resultCode == RESULT_CANCELED) { // ???????????? ????????? ?????????.

                Toast.makeText(getApplicationContext(), "?????? ?????? ????????? ??????????????????. ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();

                finish();

            }
        }


        if (requestCode == 100) {

            if (resultCode == RESULT_OK) { // ????????? ???????????? ?????? ??? ?????????

                // ????????? ?????? ?????? ?????? ?????????.
                for (CustomUserViewer viewer : addedViewerList) {

                    viewer.setVisibility(View.GONE);
                    viewer = null;

                }

                sessionInvitedMembers = data.getParcelableArrayListExtra(INVITED_MEMBER_DATA_KEY);

                // ?????? Array ??? ?????????.
                for (MoimingMembersDTO members : sessionInvitedMembers) {

                    addViewInHorizontalScroll(members); // ???????????? ?????? ????????? Horizontal ??? ?????????.
                }

                inviteNmuCnt = data.getExtras().getInt(INVITED_NMU_CNT); // ???????????? ????????? ????????????

                if (inviteNmuCnt != 0) {

                    textNmuInviteCnt.setVisibility(View.VISIBLE);
                    nmuRecycler.setVisibility(View.VISIBLE);
                    textNmuRef.setVisibility(View.VISIBLE);
                    nmuInviteViewer.setVisibility(View.VISIBLE);
                    textNmuCnt.setText("+" + inviteNmuCnt);

                } else {

                    textNmuInviteCnt.setVisibility(View.GONE);
                    nmuRecycler.setVisibility(View.GONE);
                    textNmuRef.setVisibility(View.GONE);
                    nmuInviteViewer.setVisibility(View.GONE);
                }

                // ??? ???????????? ????????? ?????? ????????????
                int cnt = 1;
                cnt += sessionInvitedMembers.size();
                cnt += inviteNmuCnt;
                textInviteCnt.setText("??? " + cnt + "???");

                if (sessionType == 0) {

                    totalCost = cnt * fundingCost;
                    btnPrevFunding.setBackgroundColor(getResources().getColor(R.color.moimingLightTheme, null));
                    btnPrevFunding.setTextColor(getResources().getColor(R.color.moimingBoldGray, null));
                    btnPrevFunding.setEnabled(false); // ?????? ?????????.

                }

                if (isReady.get(2)) { // ????????? ?????? ??? --> ????????? ????????? ???.

                    MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT = 0;
                    NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT = 0;

                    // list ??? ?????? ?????? (????????? ????????? ?????? ?????? n ???)
                    membersSessionDataList.clear();
                    nmuSessionDataList.clear();

                    buildLists();

                    // ???????????? ??????
                    int moimingCnt = sessionInvitedMembers.size() + 1;
                    textMemberInviteCnt.setText(moimingCnt + "???");
                    textNmuInviteCnt.setText(inviteNmuCnt + "???");

                    membersAdapter.setMoimingCnt(cnt);
                    nmuAdapter.setMoimingCnt(cnt);

                    membersAdapter.notifyDataSetChanged();
                    nmuAdapter.notifyDataSetChanged();

                } else {

                    initPersonalCostLayout();
                }

                // ????????? ??????.
                setReceipt();

                //3. ?????? ??????
                isReady.put(2, true); // ?????? ??? ????????? ???????????? ??????.

            }

        } else if (requestCode == 200) { // ?????? ?????? ???????????? ???.

            if (resultCode == RESULT_OK) {

                // ??????????????? ?????? ??????
                MoimingSessionVO previousSession = (MoimingSessionVO) data.getSerializableExtra(getResources().getString(R.string.moiming_session_data_key));
                boolean isAllSingleSame = data.getBooleanExtra("is_all_single_same", true);

                // 1. ?????? Setting.
                inputSessionName.setText(previousSession.getSessionName()); // ?????? ???????????? Set.
                inputSessionName.onEditorAction(EditorInfo.IME_ACTION_DONE);

                // 2. ?????? Setting.
                int singleCost = previousSession.getSingleCost();
                inputCost.setText(String.valueOf(singleCost));
                inputCost.onEditorAction(EditorInfo.IME_ACTION_DONE);

                // 3. ?????? ????????? All Set.
                // -1 sessionMemberList ??? ??????.
                for (UserSessionLinkerVO usLinker : previousSession.getUsLinkerList()) {
                    // ?????? ????????????.
                    MoimingUserVO previousSelectedUser = usLinker.getMoimingUser();
                    if (!previousSelectedUser.getUuid().equals(curUser.getUuid())) {
                        MoimingMembersDTO previousMemberDTO = new MoimingMembersDTO(previousSelectedUser.getUuid()
                                , previousSelectedUser.getUserName()
                                , previousSelectedUser.getUserPfImg());

                        sessionInvitedMembers.add(previousMemberDTO);
                        addViewInHorizontalScroll(previousMemberDTO);
                    }

                } //  ?????? ????????? ??????, horizontal view ??? ????????????.

                // -1 nmu Cnt ??????

                inviteNmuCnt = 0;

                if (previousSession.getNmuList() != null) {
                    inviteNmuCnt = previousSession.getNmuList().size();

                    if (inviteNmuCnt != 0) { // NMU Viewer (????????? ??? ?????????) ????????? ?????????
                        nmuInviteViewer.setVisibility(View.VISIBLE);
                        textNmuCnt.setText("+" + inviteNmuCnt);
                    } else {
                        nmuInviteViewer.setVisibility(View.GONE);
                        textNmuInviteCnt.setVisibility(View.GONE);
                        nmuRecycler.setVisibility(View.GONE);
                        textNmuRef.setVisibility(View.GONE);
                    }
                } else {
                    textNmuInviteCnt.setVisibility(View.GONE);
                    nmuRecycler.setVisibility(View.GONE);
                    textNmuRef.setVisibility(View.GONE);
                }

                // ??? ???????????? ????????? ?????? ????????????
                int cnt = 1;
                cnt += sessionInvitedMembers.size();
                cnt += inviteNmuCnt;
                textInviteCnt.setText("??? " + cnt + "???");

                //3. ?????? ??????
                isReady.put(2, true);

                btnPrevFunding.setBackgroundColor(getResources().getColor(R.color.moimingLightTheme, null));
                btnPrevFunding.setTextColor(getResources().getColor(R.color.moimingBoldGray, null));
                btnPrevFunding.setEnabled(false); // ?????? ?????????.
                /*btnPrevFunding.setTextColor(getResources().getColor(R.color.moimingLightTheme, null));
                btnPrevFunding.setEnabled(false); // ?????? ???????????? ?????? ?????????.*/

                totalCost = previousSession.getTotalCost();

                // 4. ????????? ?????? All Set. // ????????? ?????? ????????? ????????? ???????????? ???
                initPrevFundingLayout(previousSession);

                setReceiptForPrevFunding(cnt, isAllSingleSame);

            }

            // TODO: ????????? ????????? ?????? ???????????? ??????
            layoutDrawer.requestFocus();
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

//            View view = this.getCurrentFocus();
//
//            if (view != null) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            }


        }

    }

    private void initPrevFundingLayout(MoimingSessionVO sessionVO) {

        int moimingCnt = sessionInvitedMembers.size() + 1; // ??? ????????? ????????????
        textMemberInviteCnt.setText(moimingCnt + "???");
        textNmuInviteCnt.setText(inviteNmuCnt + "???");

        int totalCnt = moimingCnt + inviteNmuCnt;

        // ?????? ??????.
        // ?????? ?????? ?????? ????????? ????????? ??????????????? ??????.
        // 1. ?????? ????????? (???) ??? ???????????? ?????? ??????
        // ????????? totalCost ??? ?????? S/U ??????.
        boolean isCurUserPreviousMember = false;

        for (UserSessionLinkerVO sessionLinker : sessionVO.getUsLinkerList()) {
            MoimingUserVO prevMember = sessionLinker.getMoimingUser();

            SessionMemberLinkerData prevMemberData = new SessionMemberLinkerData(prevMember.getUuid()
                    , prevMember.getUserName()
                    , prevMember.getUserPfImg()
                    , sessionLinker.getPersonalCost());

            if (!sessionLinker.getPersonalCost().equals(sessionVO.getSingleCost())) { // ????????? ????????? ????????????????
                prevMemberData.setIsEdited(true); // ???????????? ??? ??? ?????????!
            }

            if (!prevMember.getUuid().equals(curUser.getUuid())) { // ??? ????????? ???????????? ?????? ??????.
                membersSessionDataList.add(prevMemberData);
            } else { // ??? ????????????.
                isCurUserPreviousMember = true;
                membersSessionDataList.add(0, prevMemberData); // ?????? ?????? ?????????.
            }
        }

        // 2. ?????? ????????? (???) ??? ???????????? ?????? ?????? ?????? --> ?????? ????????? ????????????,
        if (!isCurUserPreviousMember) {
            SessionMemberLinkerData prevMemberData = new SessionMemberLinkerData(curUser.getUuid()
                    , curUser.getUserName(), curUser.getUserPfImg(), sessionVO.getSingleCost());

            totalCost += sessionVO.getSingleCost(); // ????????? ???????????? Single Cost ?????? ?????????.
            membersSessionDataList.add(0, prevMemberData); // ?????? ????????? ?????????.

        }

        if (sessionVO.getNmuList() != null) {
            if (sessionVO.getNmuList().size() != 0) {
                for (int i = 0; i < sessionVO.getNmuList().size(); i++) {

                    NonMoimingUserVO nmu = sessionVO.getNmuList().get(i);
                    SessionNmuLinkerData preNmudata = new SessionNmuLinkerData(nmu.getNmuName(), nmu.getNmuPersonalCost());

                    if (!nmu.getNmuPersonalCost().equals(sessionVO.getSingleCost())) {
                        preNmudata.setIsEdited(true);
                    }

                    nmuSessionDataList.add(preNmudata);
                }
            }
        }


        // TODO: ?????? ??????. ??? ?????? ?????????  ??? ???????????? ?????? ???.
        LinearLayoutManager membersLayoutManager = new LinearLayoutManager(this);

        //1. ????????? ?????? ??? ??????.
        memberRecycler.setLayoutManager(membersLayoutManager);
        membersAdapter = new MembersEditPersonalCostRecyclerAdapter(getApplicationContext(), totalCnt, membersSessionDataList
                , editCostListener, cancelCheckBoxListener, cntListener);
        memberRecycler.setAdapter(membersAdapter);
        memberRecycler.addItemDecoration(new RecyclerViewItemDecoration(this, 8));


        //2. ??????????????? ??????.
        if (sessionVO.getNmuList() != null) {

            LinearLayoutManager nmuLayoutManager = new LinearLayoutManager(this);

            nmuRecycler.setLayoutManager(nmuLayoutManager);
            nmuAdapter = new NmuEditPersonalCostRecyclerAdapter(getApplicationContext(), totalCnt, nmuSessionDataList
                    , editCostListener, cancelCheckBoxListener, cntListener, nmuNameChangeListener);
            nmuRecycler.setAdapter(nmuAdapter);
            nmuRecycler.addItemDecoration(new RecyclerViewItemDecoration(this, 8));

        }
    }

    private void setReceiptForPrevFunding(int totalMemberCnt, boolean isAllSame) {

        layoutSessionResult.setVisibility(View.VISIBLE);
        btnCreationFinish.setTextColor(getResources().getColor(R.color.moimingTheme, null));
        btnCreationFinish.setEnabled(true);

        String personalCost = AppExtraMethods.wonToNormal(inputCost.getText().toString());

        resultTotalCost.setText(AppExtraMethods.moneyToWonWon(totalCost));
        resultPersonalCost.setText(AppExtraMethods.moneyToWonWon(Integer.parseInt(personalCost)));
        resultMemberCnt.setText(String.valueOf(totalMemberCnt));

        if (isAllSame) {
            findViewById(R.id.view_edited_cost).setVisibility(View.GONE);
        } else {
            findViewById(R.id.view_edited_cost).setVisibility(View.VISIBLE);
        }

    }

    private void setReceipt() {

        layoutSessionResult.setVisibility(View.VISIBLE);
        btnCreationFinish.setTextColor(getResources().getColor(R.color.moimingTheme, null));
        btnCreationFinish.setEnabled(true);

        // ?????? ??????????????? ??????.
        // 1. ??? ?????? // 2. ??? ?????? // 3. ????????? ??????. //4. ?????? ?????? ????????? +- ??????
        if (sessionType == 1) {
            resultTotalCost.setText(AppExtraMethods.moneyToWonWon(totalCost));

            int totalCnt = inviteNmuCnt + sessionInvitedMembers.size() + 1;
            resultMemberCnt.setText(String.valueOf(totalCnt));

            int dividedN = (int) (totalCost / totalCnt);
            resultPersonalCost.setText(AppExtraMethods.moneyToWonWon(dividedN));

        } else {

            int totalCnt = inviteNmuCnt + sessionInvitedMembers.size() + 1;
            int dividedN = Integer.parseInt(AppExtraMethods.wonToNormal(inputCost.getText().toString()));
            int fundingTotalCost = totalCnt * dividedN;

            resultTotalCost.setText(AppExtraMethods.moneyToWonWon(fundingTotalCost));
            resultPersonalCost.setText(AppExtraMethods.moneyToWonWon(dividedN));
            resultMemberCnt.setText(String.valueOf(totalCnt));
        }
    }

    private void refreshReceiptFundings() {

        int totalMembers = membersSessionDataList.size() + nmuSessionDataList.size();
        int totalFundingCost = totalMembers * fundingCost;

        resultTotalCost.setText(AppExtraMethods.moneyToWonWon(totalFundingCost));
        resultPersonalCost.setText(AppExtraMethods.moneyToWonWon(fundingCost));

        findViewById(R.id.view_edited_cost).setVisibility(View.GONE);
    }

    ////////////////////////////// ?????? ?????????
    private void editReceiptFundings(boolean isCheckboxListener, int editedCost, int prevCost) {

//        if (isCheckBoxListener) { // 1. ????????? ????????? ???????????? ??????.
        int diff;

        if (isCheckboxListener) {
            diff = editedCost - prevCost; // +?????? - ??????.
            totalCost += diff;
        } else {

            diff = fundingCost - prevCost;
            totalCost += diff;

        }

        resultTotalCost.setText(AppExtraMethods.moneyToWonWon(totalCost));
/*
        } else { // 2. ?????? ????????? ???????????? ??????.

            int prevEditedCost = editedCost;
            int diff = prevEditedCost - fundingCost;
        }*/

        findViewById(R.id.view_edited_cost).setVisibility(View.VISIBLE);


    }

    private void editReceiptDutchpay(boolean reset, int newDividedN) {

        resultTotalCost.setText(AppExtraMethods.moneyToWonWon(totalCost));
        resultPersonalCost.setText(AppExtraMethods.moneyToWonWon(newDividedN));

        if (reset) {

            findViewById(R.id.view_edited_cost).setVisibility(View.GONE);

        } else {

            findViewById(R.id.view_edited_cost).setVisibility(View.VISIBLE);
        }
    }


    private void initHorizontalView() {

        CustomUserViewer curUserViewer = new CustomUserViewer(this, false, null);
        curUserViewer.setTextUserName(curUser.getUserName());
        curUserViewer.setImgUserPfImg(curUser.getUserPfImg());
        layoutUsersHolder.addView(curUserViewer);
        giveMarginInScrollView(curUserViewer);

    }


    private void addViewInHorizontalScroll(MoimingMembersDTO member) {

        CustomUserViewer userViewer = new CustomUserViewer(this, true, null);

        userViewer.setTextUserName(member.getUserName());
        userViewer.setImgUserPfImg(member.getUserPfImg());
        userViewer.disableRemoverBtn();

        layoutUsersHolder.addView(userViewer);
        addedViewerList.add(userViewer);

        giveMarginInScrollView(userViewer);

    }

    private void giveMarginInScrollView(CustomUserViewer viewer) {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewer.getLayoutParams();

        params.setMargins(0, 0, dpToPx(8), 0);

        viewer.setLayoutParams(params);

    }


    private int dpToPx(int dp) {

        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private RelativeLayout layoutDrawer;
    private ConstraintLayout layoutPersonalCost;
    private TextView textMemberInviteCnt, textNmuInviteCnt; // x ???
    private TextView textNmuRef;
    private RecyclerView memberRecycler, nmuRecycler;
    private TextView btnResetPersonal;
    private View sizeMatcher;

    // ?????????
    private TextView resultMemberCnt, resultTotalCost, resultPersonalCost;

    List<SessionMemberLinkerData> membersSessionDataList;
    List<SessionNmuLinkerData> nmuSessionDataList;

    MembersEditPersonalCostRecyclerAdapter membersAdapter;
    NmuEditPersonalCostRecyclerAdapter nmuAdapter;


    private void initPersonalCostLayout() {

        // 1. ???????????? ?????????
        int moimingCnt = sessionInvitedMembers.size() + 1; // ??? ????????? ????????????
        textMemberInviteCnt.setText(moimingCnt + "???");
        textNmuInviteCnt.setText(inviteNmuCnt + "???");

        // onActivityResult ??????
        // 2. ??????????????? ??? init
        initRecyclerViews(moimingCnt + inviteNmuCnt);

    }


    private void initRecyclerViews(int totalCnt) {

        buildLists();

        LinearLayoutManager membersLayoutManager = new LinearLayoutManager(this);

        //1. ????????? ?????? ??? ??????.
        memberRecycler.setLayoutManager(membersLayoutManager);
        membersAdapter = new MembersEditPersonalCostRecyclerAdapter(getApplicationContext(), totalCnt, membersSessionDataList, editCostListener, cancelCheckBoxListener, cntListener);
        memberRecycler.setAdapter(membersAdapter);
        memberRecycler.addItemDecoration(new RecyclerViewItemDecoration(this, 8));

        //2. ??????????????? ??????.
        LinearLayoutManager nmuLayoutManager = new LinearLayoutManager(this);

        nmuRecycler.setLayoutManager(nmuLayoutManager);
        nmuAdapter = new NmuEditPersonalCostRecyclerAdapter(getApplicationContext(), totalCnt, nmuSessionDataList, editCostListener, cancelCheckBoxListener, cntListener, nmuNameChangeListener);
        nmuRecycler.setAdapter(nmuAdapter);
        nmuRecycler.addItemDecoration(new RecyclerViewItemDecoration(this, 8));

    }


    private void buildLists() {

        int dividedN;
        int curUserCost;

        if (sessionType == 1) {
            // 0. ????????? N ?????????.
            int invitedCnt = sessionInvitedMembers.size() + inviteNmuCnt + 1;
            dividedN = (int) (totalCost / invitedCnt);
            curUserCost = dividedN;

            if ((dividedN * invitedCnt) != totalCost) {

                int diff = totalCost - (dividedN * invitedCnt);
                curUserCost += diff;
            }

        } else { // ????????? ??????

            dividedN = Integer.parseInt(AppExtraMethods.wonToNormal(inputCost.getText().toString()));
            curUserCost = dividedN;

        }

        // 1. ?????? ????????? ?????? ?????? ?????????.
        SessionMemberLinkerData curUserLinkData = new SessionMemberLinkerData(curUser.getUuid(), curUser.getUserName() + " (???)", curUser.getUserPfImg(), curUserCost);
        membersSessionDataList.add(curUserLinkData);

        // ?????? ???????????? GroupMemberDTO list ??? ???????????? ??? ??????.
        // 2. ?????? ???????????? ????????????.
        for (MoimingMembersDTO invitedMember : sessionInvitedMembers) {

            SessionMemberLinkerData invitedMemberLinkerData = new SessionMemberLinkerData(invitedMember.getUuid(), invitedMember.getUserName(), invitedMember.getUserPfImg(), dividedN);
            membersSessionDataList.add(invitedMemberLinkerData);

        }

        // 3. ???????????? ???????????? ?????? ????????? ??????.
        if (inviteNmuCnt != 0) {
            for (int i = 1; i < inviteNmuCnt + 1; i++) {

                String userName = "?????? " + i;
                SessionNmuLinkerData invitedNmuLinkerData = new SessionNmuLinkerData(userName, dividedN);
                nmuSessionDataList.add(invitedNmuLinkerData);
            }
        }
    }

    public UserCostCancelCheckBoxListener cancelCheckBoxListener = new UserCostCancelCheckBoxListener() { // ?????? ?????????.
        @Override
        public void onCancelCheckbox(boolean isMoimingUser, int position, int prevFundingCost) {
            if (sessionType == 1) {
                int totalCostCal = totalCost; // ????????? ??????.
                int totalInvitedUsers = membersSessionDataList.size() + nmuSessionDataList.size();
                int newDividedN;
                int left = 0;

                // ???????????? ????????? ???????????? ?????? ????????? ?????? N?????? ??????.

                if (isMoimingUser) {

                    SessionMemberLinkerData uncheckedData = membersSessionDataList.get(position);
                    uncheckedData.setIsEdited(false);

                    membersSessionDataList.set(position, uncheckedData);

                    for (int i = 0; i < membersSessionDataList.size(); i++) { // ????????? Check ??? ????????? ?????? ????????? ????????????.

                        SessionMemberLinkerData membersData = membersSessionDataList.get(i);
                        if (membersData.getIsEdited()) {
                            totalCostCal -= membersData.getUserCost();
                            totalInvitedUsers -= 1;

                        }
                    }

                    for (SessionNmuLinkerData nmusData : nmuSessionDataList) {

                        if (nmusData.getIsEdited()) {
                            totalCostCal -= nmusData.getUserCost();
                            totalInvitedUsers -= 1;
                        }
                    }


                    newDividedN = (int) (totalCostCal / totalInvitedUsers); // ????????? ???????????? ?????? N

                    if ((newDividedN * totalInvitedUsers) != totalCostCal) {
                        left = totalCostCal - (newDividedN * totalInvitedUsers); // ?????? ???????????? ????????? ???????????? ?????? ?????? ??????
                    }

                    // N?????? ????????? ??????????????? ??????.
                    for (int i = 0; i < membersSessionDataList.size(); i++) {

                        SessionMemberLinkerData member = membersSessionDataList.get(i);

                        if (member.getIsEdited()) { // ?????? ????????? ???????

                            if (i == 0) {
                                int curUserCost = member.getUserCost();
                                member.setUserCost(curUserCost + left);
                            }

                           /* if (i == position) {
                                member.setUserCost(newDividedN);
                                member.setIsEdited(false);
                            }*/
                        } else { // N??? ?????? ?????? ?????????
                            if (i == 0) {
                                member.setUserCost(newDividedN + left);
                            }

                            member.setUserCost(newDividedN);
                        }

                        membersSessionDataList.set(i, member);
                    }

                    for (int i = 0; i < nmuSessionDataList.size(); i++) {

                        SessionNmuLinkerData nmu = nmuSessionDataList.get(i);

                        if (!nmu.getIsEdited()) { // ?????? ????????????, edit ??? ????????? ?????? ??????

                            nmu.setUserCost(newDividedN);
                        }

                        nmuSessionDataList.set(i, nmu);
                    }

                } else { // NMU ??? ??????????????? ??????!

                    SessionNmuLinkerData uncheckedData = nmuSessionDataList.get(position);
                    uncheckedData.setIsEdited(false);

                    nmuSessionDataList.set(position, uncheckedData);

                    for (int i = 0; i < membersSessionDataList.size(); i++) {

                        SessionMemberLinkerData membersData = membersSessionDataList.get(i);

                        if (membersData.getIsEdited()) {
                            totalCostCal -= membersData.getUserCost();
                            totalInvitedUsers -= 1;

                        }
                    }

                    for (SessionNmuLinkerData nmusData : nmuSessionDataList) {

                        if (nmusData.getIsEdited()) {
                            totalCostCal -= nmusData.getUserCost();
                            totalInvitedUsers -= 1;
                        }
                    }


                    newDividedN = (int) (totalCostCal / totalInvitedUsers);

                    if ((newDividedN * totalInvitedUsers) != totalCostCal) {
                        left = totalCostCal - (newDividedN * totalInvitedUsers); // ?????? ???????????? ????????? ???????????? ?????? ?????? ??????
                    }

                    for (int i = 0; i < membersSessionDataList.size(); i++) {

                        SessionMemberLinkerData member = membersSessionDataList.get(i);

                        if (member.getIsEdited()) {
                            // ????????????.
                            if (i == 0) {
                                int curUserCost = member.getUserCost();
                                member.setUserCost(curUserCost + left);
                            }

                        } else {
                            if (i == 0) {
                                member.setUserCost(newDividedN + left);
                            }
                            member.setUserCost(newDividedN);

                        }
                        membersSessionDataList.set(i, member);
                    }

                    for (int i = 0; i < nmuSessionDataList.size(); i++) {

                        SessionNmuLinkerData nmu = nmuSessionDataList.get(i);

                        if (!nmu.getIsEdited()) {
                            nmu.setUserCost(newDividedN);
                        }

                        /*if (i == position) {
                            nmu.setUserCost(newDividedN);
                            nmu.setIsEdited(false);
                        }
*/
                        nmuSessionDataList.set(i, nmu);
                    }

                }

                editReceiptDutchpay(false, newDividedN);

            } else { // ????????? ??????

                // ?????? ?????? ????????? ???????????? ?????????.
                if (isMoimingUser) {

                    SessionMemberLinkerData userData = membersSessionDataList.get(position);
                    userData.setUserCost(fundingCost); // ?????? fundingCost ??? ????????????.
                    userData.setIsEdited(false);

                    membersSessionDataList.set(position, userData);

                } else {

                    SessionNmuLinkerData nmuData = nmuSessionDataList.get(position);
                    nmuData.setUserCost(fundingCost);
                    nmuData.setIsEdited(false);

                    nmuSessionDataList.set(position, nmuData);
                }

                editReceiptFundings(false, 0, prevFundingCost);

            }

            membersAdapter.notifyDataSetChanged();
            nmuAdapter.notifyDataSetChanged();


        }
    };


    public UserEditPersonalCostListener editCostListener = new UserEditPersonalCostListener() {
        @Override
        public void onFinishEditing(boolean isMoimingUser, int position, int personalCost, int prevUserCost) {

            int prevFundingCost = 0;

            if (sessionType == 1) { // ??????????????? ??????
                int totalCostCal = totalCost; // ????????? ??????.
                int totalInvitedUsers = membersSessionDataList.size() + nmuSessionDataList.size();
                int newDividedN;
                int left = 0;

                // ???????????? ??? ?????? ???????????? ???????????? ???.
                // 1. ?????? ????????? ????????? ????????????.
                if (isMoimingUser) {

                    SessionMemberLinkerData userData = membersSessionDataList.get(position);
                    userData.setUserCost(personalCost);
                    userData.setIsEdited(true);

                    membersSessionDataList.set(position, userData);

                    for (SessionMemberLinkerData membersData : membersSessionDataList) {

                        if (membersData.getIsEdited()) {
                            totalCostCal -= membersData.getUserCost();
                            totalInvitedUsers -= 1;
                        }
                    }

                    for (SessionNmuLinkerData nmusData : nmuSessionDataList) {

                        if (nmusData.getIsEdited()) {
                            totalCostCal -= nmusData.getUserCost();
                            totalInvitedUsers -= 1;
                        }
                    }


                    if (totalCostCal < 0) {
                        // ????????? ????????????

                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();

                        userData.setUserCost(prevUserCost);
                        userData.setIsEdited(false);


                        membersAdapter.notifyDataSetChanged();
                        return;

                    }


                    if (totalInvitedUsers == 0) { // ??????????????? ???????????????
                        // ??? ?????? ????????? ????????? ?????? ???. left ??? ???????????? ???????????? ???.
                        SessionMemberLinkerData curUserData = membersSessionDataList.get(0);
                        int curUserCost = curUserData.getUserCost();
                        curUserData.setUserCost(curUserCost + totalCostCal);

                        membersSessionDataList.set(0, curUserData);

                        membersAdapter.notifyDataSetChanged();

                        return;

                    } else {

                        newDividedN = (int) (totalCostCal / totalInvitedUsers);

                        if ((newDividedN * totalInvitedUsers) != totalCostCal) {
                            left = totalCostCal - (newDividedN * totalInvitedUsers);
                        }

                    }


                    for (int i = 0; i < membersSessionDataList.size(); i++) {
                        // ?????? ????????? ?????? ????????? ???????
                        SessionMemberLinkerData member = membersSessionDataList.get(i);

                        if (member.getIsEdited()) {
                            // ????????????.
                            if (i == 0) {
                                int curUserCost = member.getUserCost();
                                member.setUserCost(curUserCost + left);
                            }

                        } else {
                            if (i == 0) {
                                member.setUserCost(newDividedN + left);
                            }
                            member.setAutoChanged(true);
                            member.setUserCost(newDividedN);
                        }

                        membersSessionDataList.set(i, member);
                    }

                    for (int i = 0; i < nmuSessionDataList.size(); i++) {

                        SessionNmuLinkerData nmu = nmuSessionDataList.get(i);

                        if (!nmu.getIsEdited()) {
                            nmu.setAutoChanged(true);
                            nmu.setUserCost(newDividedN);
                        }

                        nmuSessionDataList.set(i, nmu);
                    }


                } else { // ??????????????? ???????????? ????????????.

                    SessionNmuLinkerData nmuData = nmuSessionDataList.get(position);
                    nmuData.setUserCost(personalCost);
                    nmuData.setIsEdited(true);

                    nmuSessionDataList.set(position, nmuData);

                    for (SessionMemberLinkerData membersData : membersSessionDataList) {

                        if (membersData.getIsEdited()) {
                            totalCostCal -= membersData.getUserCost();
                            totalInvitedUsers -= 1;
                        }
                    }

                    for (SessionNmuLinkerData nmusData : nmuSessionDataList) {

                        if (nmusData.getIsEdited()) {
                            totalCostCal -= nmusData.getUserCost();
                            totalInvitedUsers -= 1;
                        }
                    }

                    if (totalCostCal < 0) {
                        // ????????? ????????????

                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();

                        nmuData.setUserCost(prevUserCost);
                        nmuData.setIsEdited(false);

                        nmuAdapter.notifyDataSetChanged();

                        return;

                    }

                    if (totalInvitedUsers == 0) {
                        // ??? ?????? ????????? ????????? ?????? ???. left ??? ???????????? ???????????? ???.

                        SessionMemberLinkerData curUserData = membersSessionDataList.get(0);
                        int curUserCost = curUserData.getUserCost();
                        curUserData.setUserCost(curUserCost + totalCostCal);

                        membersSessionDataList.set(0, curUserData);

                        membersAdapter.notifyDataSetChanged();

                        return;

                    } else {
                        newDividedN = (int) (totalCostCal / totalInvitedUsers);

                        if ((newDividedN * totalInvitedUsers) != totalCostCal) {
                            left = totalCostCal - (newDividedN * totalInvitedUsers);
                        }

                    }

                    for (int i = 0; i < membersSessionDataList.size(); i++) {
                        // ?????? ????????? ?????? ????????? ???????
                        SessionMemberLinkerData member = membersSessionDataList.get(i);

                        if (member.getIsEdited()) {
                            // ????????????.
                            if (i == 0) {
                                int curUserCost = member.getUserCost();
                                member.setUserCost(curUserCost + left);
                            }

                        } else {
                            if (i == 0) {
                                member.setUserCost(newDividedN + left);
                            }
                            member.setAutoChanged(true);
                            member.setUserCost(newDividedN);
                        }

                        membersSessionDataList.set(i, member);
                    }

                    for (int i = 0; i < nmuSessionDataList.size(); i++) {

                        SessionNmuLinkerData nmu = nmuSessionDataList.get(i);

                        if (!nmu.getIsEdited()) {
                            nmu.setAutoChanged(true);
                            nmu.setUserCost(newDividedN);
                        }

                        nmuSessionDataList.set(i, nmu);
                    }

                }

                editReceiptDutchpay(false, newDividedN);

            } else { // ????????? ??????: ????????? ????????? ???????????? ??????, ??? ???????????? ???????????? ??????. position, personalCost

                if (isMoimingUser) {

                    SessionMemberLinkerData userData = membersSessionDataList.get(position);

                    prevFundingCost = userData.getUserCost();

                    userData.setUserCost(personalCost);
                    userData.setIsEdited(true);

                    membersSessionDataList.set(position, userData);

                } else {


                    SessionNmuLinkerData nmuData = nmuSessionDataList.get(position);

                    prevFundingCost = nmuData.getUserCost();

                    nmuData.setUserCost(personalCost);
                    nmuData.setIsEdited(true);

                    nmuSessionDataList.set(position, nmuData);
                }

                editReceiptFundings(true, personalCost, prevFundingCost);

            }

            String s = "";

            for (int i = 0; i < membersSessionDataList.size(); i++) {

                SessionMemberLinkerData data = membersSessionDataList.get(i);

                s += "[??????" + i + "]"
                        + "\n??????: " + data.getUserName()
                        + "\n??????: " + String.valueOf(data.getUserCost())
                        + "\n????????????: " + String.valueOf(data.getIsEdited())
                        + "\n";
            }

            Log.w("TAG", s);

            membersAdapter.notifyDataSetChanged();
            nmuAdapter.notifyDataSetChanged();
        }
    };


    public void scrollToView(View view, final NestedScrollView scrollView, int count) {
        if (view != null && view != scrollView) {
            count += view.getTop();
            scrollToView((View) view.getParent(), scrollView, count);
        } else if (scrollView != null) {
            final int finalCount = count;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, finalCount);
                }
            }, 200);
        }
    }


    private MoimingSessionVO createdSession;


    // TODO : ????????? ??????????????? ????????? ?????? singleCost ??? ????????? ???.
    //       ?????? prevFunding ??? ???????????? ????????????????????? ?????? ??????.
    //       ?????? ????????? ??? ?????? ??? ???????????? ?????????. (prevFunding ?????? ????????? ??????????????? ?????????;)

    private MoimingSessionRequestDTO makeSessionRequestData() {

        // 1. Session ?????? ??????.
        String sessionName = inputSessionName.getText().toString();

        int sessionMemberCnt = membersSessionDataList.size() + nmuSessionDataList.size();
        int totalSessionCost;
        int singleCost;
        if (sessionType == 1) {

            String curText = inputCost.getText().toString();
            String deleteWon = AppExtraMethods.wonToNormal(curText);
            totalSessionCost = Integer.parseInt(deleteWon);
            // ?????? ???????????? ???????????? n??? ?????? ??????

            singleCost = Integer.parseInt(AppExtraMethods.wonToNormal(resultPersonalCost.getText().toString()));
        } else {
            totalSessionCost = totalCost;
            // ???????????? ?????? ??????!
            singleCost = Integer.parseInt(AppExtraMethods.wonToNormal(inputCost.getText().toString()));
        }
        if (sessionName.isEmpty() || sessionMemberCnt == 0 || totalCost == 0) {
            Toast.makeText(getApplicationContext(), "???????????? ???????????? ????????? ??? ????????????. ????????? ????????? ?????????", Toast.LENGTH_SHORT).show();
            return null;
        }

        Integer creatorCost = membersSessionDataList.get(0).getUserCost();

        if (fromNewGroupCreation) { // Creator Cost ??? ????????? ?????? ???????????? ???????????? ????????? ?????? ????????? ??????????????? ?????? ?????????.

            return new MoimingSessionRequestDTO(curUser.getUuid(), null, sessionName, creatorCost, sessionType, sessionMemberCnt, totalSessionCost, singleCost);

        } else {

            return new MoimingSessionRequestDTO(curUser.getUuid(), curGroup.getUuid(), sessionName, creatorCost, sessionType, sessionMemberCnt, totalSessionCost, singleCost);

        }


    }

    private void requestSessionBuild() {

        // ?????? ???????????? ?????? ?????? ??????, ?????? ??? ?????????. 1// SessionRequestDTO
        MoimingSessionRequestDTO sessionRequestDTO = makeSessionRequestData();

        if (sessionRequestDTO != null) {

            TransferModel<MoimingSessionRequestDTO> transferModel = new TransferModel<>(sessionRequestDTO);
            SessionRetrofitService sessionRetrofitService = GlobalRetrofit.getInstance().getRetrofit().create(SessionRetrofitService.class);

            sessionRetrofitService.sessionCreationRequest(transferModel)
                    .subscribeOn(Schedulers.io()) // ????????? ??????????????? ????????????.
                    .observeOn(AndroidSchedulers.mainThread()) // SubscribeOn ??? UI update ?????? ?????? ???????????? ???
                    .subscribe(new Observer<TransferModel<MoimingSessionResponseDTO>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull TransferModel<MoimingSessionResponseDTO> responseDTO) {

                            // ???????????? ??? ????????? ??????
                            createdSession = responseDTO.getData().convertToVO();

                            Log.w(SESSION_CREATION_TAG, createdSession.toString());

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                            if (e.getMessage() != null) {
                                Log.e(SESSION_CREATION_TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onComplete() {

                            makeLinkerRequest();

                        }
                    });
        } else {

            Toast.makeText(getApplicationContext(), "???????????? ???????????? ????????? ??? ????????????. ????????? ????????? ?????????", Toast.LENGTH_SHORT).show();

            return;

        }
    }

    private void makeLinkerRequest() {

        // 2. USLinker ?????? ??????.
        // 2-1) MembersLinkers
        List<USLinkerRequestDTO> linkerCreationRequests = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < membersSessionDataList.size(); i++) {

            SessionMemberLinkerData memberData = membersSessionDataList.get(i);

            USLinkerRequestDTO requestDTO = new USLinkerRequestDTO(createdSession.getUuid(), memberData.getUuid(), true, memberData.getUserName(), memberData.getUserCost());

            linkerCreationRequests.add(requestDTO);
        }

        // 2-2) NmuLinkers
        for (int i = 0; i < nmuSessionDataList.size(); i++) {

            SessionNmuLinkerData nmuData = nmuSessionDataList.get(i);

            USLinkerRequestDTO nmuRequestDTO = new USLinkerRequestDTO(createdSession.getUuid(), false, nmuData.getUserName(), nmuData.getUserCost());

            linkerCreationRequests.add(nmuRequestDTO);

        }


        // Linker Request ??? ?????????.
        USLinkerRetrofitService usLinkRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(USLinkerRetrofitService.class);
        TransferModel<List<USLinkerRequestDTO>> linkRequest = new TransferModel<>(linkerCreationRequests);

        usLinkRetrofit.usLinkRequest(linkRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel transferModel) {


                        // ????????? ????????? ????????? ?????? // ?????? ??????????????? ????????? ?????? ???????????? ????????????.
                        // ?????? ????????? ?????? ????????? ????????? ???.
                        // ?????? ???????????? ?????? ?????? ??????

                        if (!fromNewGroupCreation) {
                            if (!fromMainActivity) { // ???????????? ?????? ?????? ????????? ????????? GroupActivity ??? ????????? ??????.
                                GroupActivity.SESSION_LIST_REFRESH_FLAG = true;
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                        Intent toSession = new Intent(SessionCreationActivity.this, SessionActivity.class);

                        toSession.putExtra("cur_user_status", 0); // ?????? ?????? ?????????
                        toSession.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                        toSession.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                        toSession.putExtra(GroupActivity.MOIMING_SESSION_DATA_KEY, createdSession);

                        startActivity(toSession);

                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {

        btnBack.performClick();
    }

}