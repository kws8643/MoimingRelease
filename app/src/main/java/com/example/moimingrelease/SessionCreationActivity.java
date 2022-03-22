package com.example.moimingrelease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_adapter.MembersEditPersonalCostRecyclerAdapter;
import com.example.moimingrelease.app_adapter.NmuEditPersonalCostRecyclerAdapter;
import com.example.moimingrelease.app_adapter.RecyclerViewItemDecoration;
import com.example.moimingrelease.app_listener_interface.RegisterAccountListener;
import com.example.moimingrelease.app_listener_interface.SessionCreationNmuNameChangeListener;
import com.example.moimingrelease.app_listener_interface.SessionPersonalCostEditCntListener;
import com.example.moimingrelease.app_listener_interface.UserCostCancelCheckBoxListener;
import com.example.moimingrelease.app_listener_interface.UserEditPersonalCostListener;
import com.example.moimingrelease.fragments_main.MoimingHomeFragment;
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

// TODO 더치페이 먼저.
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
    private ScrollView scroll;

    private Map<Integer, Boolean> isReady;
    public boolean isPersonalEditOpen = false;
    private boolean fromGroupActivity;
    private boolean fromNewGroupCreation;
    MoimingUserVO curUser;
    MoimingGroupVO curGroup;
    List<MoimingMembersDTO> curGroupMembers;
    List<MoimingMembersDTO> sessionInvitedMembers;
    public int inviteNmuCnt = 0;
    public int totalCost = 0;
    public int fundingCost = 0;

    private List<CustomUserViewer> addedViewerList; // 없애고 생성하고를 위해

    private SessionCreationNmuNameChangeListener nmuNameChangeListener = new SessionCreationNmuNameChangeListener() { //바뀌는 값 실시간 반영
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

            if (isMoimingUser) { // Nmu Cnt 필요

                nmuAdapter.setNmuEditCostUserCnt();

            } else { // Moiming Cnt 필요

                membersAdapter.setMoimingEditCostUserCnt();

            }

        }
    };

    TextView.OnEditorActionListener inputListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            // 두 개로 나눠서 진행. v.get ID 를 통해서!
            String inputData = v.getText().toString();
            int id = v.getId();

            switch (id) {
                case R.id.input_session_name:

                    if (inputSessionName.getText().toString().isEmpty()) {

                        Toast.makeText(getApplicationContext(), "정산이름을 입력해주세요", Toast.LENGTH_SHORT).show();

                    } else { // 넘어감.

//                        if (!isReady.get(0)) {
                        //1. EDIT TEXT 다음 액션으로
                        inputSessionName.setEnabled(false);
                        btnEditSessionName.setVisibility(View.VISIBLE);

                        if (!isReady.get(0)) {
                            inputCost.setEnabled(true);
                            inputCost.requestFocus();
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                            //2. 색깔 바꾸기
                            TextView text2 = findViewById(R.id.text_2);
                            text2.setBackground(getResources().getDrawable(R.drawable.shape_round_bg_lightpurple, null));
//                        text2.setTextColor(getResources().getColor(R.color.moimingWhite, null));

                            isReady.put(0, true);
                        }
//
                    }
                    break;

                case R.id.input_cost:

                    layoutSetMembers.performClick(); // 저걸 누르는 것과 같은 액션을 주겠음.

                    break;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_layout);

        receiveIntent();

        initView();

        initParams();

        initHorizontalView();

        COST_EDIT_USER_CNT = 0;

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

                    Toast.makeText(getApplicationContext(), "필수 항목들을 모두 입력 후 진행하여 주세요.", Toast.LENGTH_SHORT).show();
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


        inputCost.setOnTouchListener(new View.OnTouchListener() { // 이름을 완성시키는 부분
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {

                    if (inputSessionName.getText().toString().isEmpty()) {

                        Toast.makeText(getApplicationContext(), "정산이름을 입력해주세요", Toast.LENGTH_SHORT).show();

                    } else { // 넘어감.

                        if (!isReady.get(0)) {
                            //1. EDIT TEXT 다음 액션으로
                            inputCost.setEnabled(true);
                            inputCost.requestFocus();
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                            inputSessionName.setEnabled(false);
                            btnEditSessionName.setVisibility(View.VISIBLE);

                            //2. 색깔 바꾸기
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


        layoutSetMembers.setOnClickListener(new View.OnClickListener() { // Cost 를 완성시키는 부분.
            @Override
            public void onClick(View v) {

                String inputData = inputCost.getText().toString();

                if (inputData.isEmpty() || Integer.parseInt(inputData) == 0) {

                    Toast.makeText(getApplicationContext(), "정산 금액을 입력해주세요", Toast.LENGTH_SHORT).show();

                } else {

                    // 키보드 내리기
                    if (!isReady.get(1)) { // 처음 완성시키는 경우

                        if (sessionType == 1) { // 더치페이일 경우

                            totalCost = Integer.parseInt(inputData); // 모으는 총액

                        } else { // 모금일 경우

                            fundingCost = Integer.parseInt(inputData);
                        }

                        // 1. EDIT TEXT 넘기기
                        inputCost.setEnabled(false);
                        btnEditCost.setVisibility(View.VISIBLE);
                        textCostType.setVisibility(View.VISIBLE);

                        // 다음 칸 색칠하기
                        TextView text3 = findViewById(R.id.text_3);
                        text3.setBackground(getResources().getDrawable(R.drawable.shape_round_bg_lightpurple, null));
                        TextView text3_1 = findViewById(R.id.text_3_1);
                        text3_1.setTextColor(getResources().getColor(R.color.textDark, null));

                        isReady.put(1, true);

                    } else { // 금액이 수정되는 경우

                        if (sessionType == 1) { // 더치페이일 경우

                            totalCost = Integer.parseInt(inputData); // 모으는 총액

                        } else { // 모금일 경우

                            fundingCost = Integer.parseInt(inputData);
                        }

                        inputCost.setEnabled(false);
                        btnEditCost.setVisibility(View.VISIBLE);
                        textCostType.setVisibility(View.VISIBLE);

                        if (isReady.get(2)) { // MEMBER들을 한 번 설정한 상태에서 금액을 변경할 경우
//                            setReceipt();
                            refreshAllSessionByTotalCost();
                            findViewById(R.id.view_edited_cost).setVisibility(View.GONE);

                        }
                    }
                }
            }
        });


        layoutPersonalCost.setOnClickListener(new View.OnClickListener() { // 개인별 금액 수정으로 넘어간다.
            @Override
            public void onClick(View v) {

                if (!isReady.get(2)) {

                    Toast.makeText(v.getContext(), "필수인 항목들 모두 설정 후 진행하여 주세요", Toast.LENGTH_SHORT).show();

                } else {


                    if (!isPersonalEditOpen) { // 처음 들어오는 거임

                        scrollToView(layoutPersonalCost, scroll, 0);

                        TextView text4_1 = findViewById(R.id.text_4_1);
                        text4_1.setTextColor(getResources().getColor(R.color.textDark, null));
                        findViewById(R.id.view_4).setBackground(getResources().getDrawable(R.drawable.shape_round_bg_lightpurple, null));


                        // 생성된 Edit Text 포커싱 제거
                        layoutPersonalCost.setFocusable(true);
                        layoutPersonalCost.setFocusableInTouchMode(true);

                        isPersonalEditOpen = true;

                        //숨겨놨던 뷰 열기
                        layoutDrawer.setVisibility(View.VISIBLE);
                        sizeMatcher.setVisibility(View.GONE);
                        btnResetPersonal.setVisibility(View.VISIBLE);
                    }

                }
            }
        });


        // 숨겨놨던 버튼 (개인 계산 초기화 버튼)
        btnResetPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setTitle("개인별 금액 초기화").setMessage("모든 금액이 다시 1/N 됩니다. 진행하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT = 0;
                                NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT = 0;

                                int invitedCnt = sessionInvitedMembers.size() + inviteNmuCnt + 1;
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

                                membersAdapter.notifyDataSetChanged();
                                nmuAdapter.notifyDataSetChanged();

                                findViewById(R.id.view_edited_cost).setVisibility(View.GONE);


                                dialog.dismiss();
                                dialog = null;
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
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

                    if (!fromNewGroupCreation) { // 일반적인 생성

                        requestSessionBuild();

                    } else {

                        //TODO 새 그룹으로 더치페이 하기일 경우 아예 그룹부터 다 만들어야 함
                        createNewGroupAndSession();

                    }

                } else {

                    Toast.makeText(v.getContext(), "필수 항목들을 진행하여 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnPrevFunding.setOnClickListener(new View.OnClickListener() { // Group 전달이 필요.
            @Override
            public void onClick(View v) {

                Intent prevFundingIntent = new Intent(SessionCreationActivity.this, SessionCreationPreviousFundingActivity.class);

                // user, group 전달 필요
                prevFundingIntent.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                prevFundingIntent.putExtra(getResources().getString(R.string.moiming_group_data_key), (Parcelable) curGroup);

                startActivityForResult(prevFundingIntent, 200);

            }
        });

    }

    // TODO: 바로 만들기일 때, 그룹 > 그룹 연결 > 정산 > 정산 연결 순으로 생성시킨다

    private MoimingGroupAndMembersDTO response;

    private void createNewGroupAndSession() { // 더치페이 바로 시작일 때, 해당 세션으로 그룹을 만든다.
//    public MoimingGroupRequestDTO(String groupName, String groupInfo, UUID groupCreatorUuid, String bgImg, Integer groupMemberCnt) {

        String groupName = inputSessionName.getText().toString();

        int moimingMemberCnt = membersSessionDataList.size();
        // 한 객체를 만든다.
        List<UUID> membersUuidList = new ArrayList<>();

        for (SessionMemberLinkerData memberData : membersSessionDataList) { // 생성한 사람도 포함되어 있음.
            UUID memberUuid = memberData.getUuid();
            membersUuidList.add(memberUuid);
        }
        // 1. 그룹을 만든다.
        MoimingGroupRequestDTO groupRequest = new MoimingGroupRequestDTO(groupName, null, curUser.getUuid(), null, moimingMemberCnt);

        // 2. 초대 인원을 가지고 그룹원을 만든다.
        // 3. 정산활동을 그대로 만든다.
        MoimingSessionRequestDTO sessionRequest = makeSessionRequestData();

        // 4. 정산활동 정보 리스트 들을 만든다.
        List<USLinkerRequestDTO> usDataList = new ArrayList<>();

        // 4-1) Moiming User 정산 정보
        for (int i = 0; i < membersSessionDataList.size(); i++) {

            SessionMemberLinkerData memberData = membersSessionDataList.get(i);

            USLinkerRequestDTO requestDTO = new USLinkerRequestDTO(null, memberData.getUuid(), true, memberData.getUserName(), memberData.getUserCost());

            usDataList.add(requestDTO);
        }

        // 2-2) NMU 정산 정보
        for (int i = 0; i < nmuSessionDataList.size(); i++) {

            SessionNmuLinkerData nmuData = nmuSessionDataList.get(i);

            USLinkerRequestDTO nmuRequestDTO = new USLinkerRequestDTO(null, false, nmuData.getUserName(), nmuData.getUserCost());

            usDataList.add(nmuRequestDTO);

        }

        // TODO: 그룹 > 그룹 연결 > 정산 > 정산 연결 순으로 생성시키기 위해 한꺼번에 보낸다.
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

                        // 액티비티를 종료 후 Group Activity 로 이동시킨다.
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


    // 다 준비된 후에 가격을 조정할 경우
    private void refreshAllSessionByTotalCost() {

        int newDividedN;
        int invitedCnt = sessionInvitedMembers.size() + inviteNmuCnt + 1;

        if (sessionType == 1) {

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

        } else { // 모금일 경우


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


    private void receiveIntent() {

        Intent dataIntent = getIntent();

        sessionType = dataIntent.getExtras().getInt(getResources().getString(R.string.session_creation_type_key));
        curGroup = (MoimingGroupVO) dataIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));
        curUser = (MoimingUserVO) dataIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        curGroupMembers = dataIntent.getParcelableArrayListExtra(GroupActivity.MOIMING_GROUP_MEMBERS_KEY);
        fromGroupActivity = dataIntent.getExtras().getBoolean(getResources().getString(R.string.session_creation_from_group_activity_flag), false);

        if (curUser.getBankName() == null || curUser.getBankNumber() == null) {

            showRegisterAccountDialog();
        }
        // 메인에서 바로 생성중일 경우.
        // 제어되어야 하는 것.
        // 1. 무조건 더치페이
        // 2. GroupMembers 들이 카카오 친구들이여야 한다. <여기까지 된듯>
        // 3. 생성시 Group 생성 --> Session 생성 --> SessionLinker 생성
        // 이후로 차이 없음.
        fromNewGroupCreation = dataIntent.getExtras().getBoolean(getResources().getString(R.string.session_creation_with_new_group_flag), false);

        if (fromNewGroupCreation) { // 바로 생성일 경우
            sessionType = 1;
            curGroup = null;
            curGroupMembers = null;

            KakaoMoimingFriends kmf = KakaoMoimingFriends.getInstance(); // 카카오 친구들을 미리 한번 호출해준다. // List 준비

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

                Toast.makeText(getApplicationContext(), "계좌 등록이 되어야 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();

                finish();

            }
        }
    };

    private void showRegisterAccountDialog() {

        RegisterAccountDialog registerDialog = new RegisterAccountDialog(this, registerAccountListener);

        registerDialog.show();

    }

    private void initView() {

        //NMU
        nmuInviteViewer = findViewById(R.id.viewer_non_moiming_member_out);
        textNmuCnt = findViewById(R.id.text_nmu_cnt_out);

        // 계산서 뷰
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

        textInviteCnt = findViewById(R.id.text_invite_cnt_out); //  HZV 위에 있는 CNT

        inputCost = findViewById(R.id.input_cost);
        inputCost.setOnEditorActionListener(inputListener);

        inputSessionName = findViewById(R.id.input_session_name);
        inputSessionName.setOnEditorActionListener(inputListener);

        layoutSetMembers = findViewById(R.id.layout_3);

        scroll = findViewById(R.id.session_creation_scroll);


        // 개인별 금액 조정 뷰.
        layoutDrawer = findViewById(R.id.layout_drawer);
        textMemberInviteCnt = findViewById(R.id.text_member_invite_cnt);
        textNmuInviteCnt = findViewById(R.id.text_nmu_invite_cnt);
        memberRecycler = findViewById(R.id.invite_session_member_recycler);
        nmuRecycler = findViewById(R.id.invite_session_nmu_recycler);
        sizeMatcher = findViewById(R.id.size_matcher);
        btnResetPersonal = findViewById(R.id.btn_reset);

        // 키보드 제어
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        if (sessionType == 0) { // 모금

            // 1. 제목 text
            textSessionType.setText("새 모금 생성");

            // 2. 금액 text
            inputSessionName.setHint("모금 이름 입력");
            inputCost.setHint("모금 인당 금액 입력");
            textCostType.setText("인당 금액 (원)");

            // 3. 이전 모금 조회 버튼
            btnPrevFunding.setVisibility(View.VISIBLE);


        } else { // 더치페이

            // 1. 제목 text
            textSessionType.setText("새 더치페이 생성");

            // 2. 금액 text
            inputSessionName.setHint("더치페이 이름 입력");
            inputCost.setHint("더치페이 총 금액 입력");
            textCostType.setText("총 금액 (원)");

            // 3. 이전 모금 조회 버튼
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

        if (requestCode == 10) { // 이건 따로! RegisterBankAccount용

            if (resultCode == RESULT_OK) {

                // 유저 계좌등록 및 업데이트 완료!
                curUser = (MoimingUserVO) data.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));

                Toast.makeText(getApplicationContext(), "계좌가 성공적으로 등록되었습니다. 정산 활동을 시작해보세요!", Toast.LENGTH_SHORT).show();


            } else if (resultCode == RESULT_CANCELED) { // 계좌정보 등록에 실패함.

                Toast.makeText(getApplicationContext(), "계좌 정보 등록에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();

                finish();

            }
        }


        if (requestCode == 100) {

            if (resultCode == RESULT_OK) { // 받아온 데이터는 현재 기 선택된

                // 처음에 모든 뷰를 리셋 해야함.
                for (CustomUserViewer viewer : addedViewerList) {

                    viewer.setVisibility(View.GONE);
                    viewer = null;

                }

                sessionInvitedMembers = data.getParcelableArrayListExtra(INVITED_MEMBER_DATA_KEY);

                // 해당 Array 로 최신화.
                for (MoimingMembersDTO members : sessionInvitedMembers) {

                    addViewInHorizontalScroll(members); // 어레이에 있는 뷰들을 Horizontal 에 주겠음.
                }

                inviteNmuCnt = data.getExtras().getInt(INVITED_NMU_CNT); // 비모이밍 멤버들 업데이트

                if (inviteNmuCnt != 0) {

                    nmuInviteViewer.setVisibility(View.VISIBLE);
                    textNmuCnt.setText("+" + inviteNmuCnt);

                } else {

                    nmuInviteViewer.setVisibility(View.GONE);
                }

                // 총 참여하는 멤버들 숫자 업데이트
                int cnt = 1;
                cnt += sessionInvitedMembers.size();
                cnt += inviteNmuCnt;
                textInviteCnt.setText("총 " + cnt + "명");

                if (sessionType == 0) {

                    totalCost = cnt * fundingCost;
                    btnPrevFunding.setBackgroundColor(getResources().getColor(R.color.moimingLightTheme, null));
                    btnPrevFunding.setTextColor(getResources().getColor(R.color.moimingBoldGray, null));
                    btnPrevFunding.setEnabled(false); // 이제 못누름.

                }

                if (isReady.get(2)) { // 처음이 아닌 것 --> 최신화 해주면 됨.

                    MembersEditPersonalCostRecyclerAdapter.MOIMING_EDIT_COST_USER_CNT = 0;
                    NmuEditPersonalCostRecyclerAdapter.NMU_EDIT_COST_USER_CNT = 0;

                    // list 들 각각 수정 (추가될 시에는 다시 전체 n 빵)
                    membersSessionDataList.clear();
                    nmuSessionDataList.clear();

                    buildLists();

                    // 텍스트들 수정
                    int moimingCnt = sessionInvitedMembers.size() + 1;
                    textMemberInviteCnt.setText(moimingCnt + "명");
                    textNmuInviteCnt.setText(inviteNmuCnt + "명");

                    membersAdapter.setMoimingCnt(cnt);
                    nmuAdapter.setMoimingCnt(cnt);

                    membersAdapter.notifyDataSetChanged();
                    nmuAdapter.notifyDataSetChanged();

                } else {

                    initPersonalCostLayout();
                }

                // 계산서 수정.
                setReceipt();

                //3. 준비 완료
                isReady.put(2, true); // 한번 다 완료한 상태임을 저장.

            }

        } else if (requestCode == 200) { // 이전 모금 불러오는 것.

            if (resultCode == RESULT_OK) {

                // 불러오려는 세션 정보
                MoimingSessionVO previousSession = (MoimingSessionVO) data.getSerializableExtra(getResources().getString(R.string.moiming_session_data_key));

                // 1. 이름 Setting.
                inputSessionName.setText(previousSession.getSessionName()); // 이름 동일하게 Set.
                inputSessionName.onEditorAction(EditorInfo.IME_ACTION_DONE);

                // 2. 가격 Setting.
                int singleCost = previousSession.getSingleCost();
                inputCost.setText(String.valueOf(singleCost));
                inputCost.onEditorAction(EditorInfo.IME_ACTION_DONE);


                // 3. 참여 멤버들 All Set.
                // -1 sessionMemberList 에 추가.
                for (UserSessionLinkerVO usLinker : previousSession.getUsLinkerList()) {
                    // 나는 제외한다.
                    MoimingUserVO previousSelectedUser = usLinker.getMoimingUser();
                    if (!previousSelectedUser.getUuid().equals(curUser.getUuid())) {
                        MoimingMembersDTO previousMemberDTO = new MoimingMembersDTO(previousSelectedUser.getUuid()
                                , previousSelectedUser.getUserName()
                                , previousSelectedUser.getUserPfImg());

                        sessionInvitedMembers.add(previousMemberDTO);
                        addViewInHorizontalScroll(previousMemberDTO);
                    }

                } //  세션 초대에 넣고, horizontal view 에 추가한다.

                // -1 nmu Cnt 추가
                inviteNmuCnt = previousSession.getNmuList().size();

                if (inviteNmuCnt != 0) { // NMU Viewer (보라색 몇 명인지) 가리고 채우고
                    nmuInviteViewer.setVisibility(View.VISIBLE);
                    textNmuCnt.setText("+" + inviteNmuCnt);
                } else {
                    nmuInviteViewer.setVisibility(View.GONE);
                }

                // 총 참여하는 멤버들 숫자 업데이트
                int cnt = 1;
                cnt += sessionInvitedMembers.size();
                cnt += inviteNmuCnt;
                textInviteCnt.setText("총 " + cnt + "명");

                //3. 준비 완료
                isReady.put(2, true);

                btnPrevFunding.setBackgroundColor(getResources().getColor(R.color.moimingLightTheme, null));
                btnPrevFunding.setTextColor(getResources().getColor(R.color.moimingBoldGray, null));
                btnPrevFunding.setEnabled(false); // 이제 못누름.
                /*btnPrevFunding.setTextColor(getResources().getColor(R.color.moimingLightTheme, null));
                btnPrevFunding.setEnabled(false); // 이제 불러오기 버튼 못누름.*/

                // 4. 멤버별 가격 All Set. //개인별 적용 가격이 있는지 들어가야 함
                initPrevFundingLayout(previousSession);

                setReceiptForPrevFunding(cnt);

            }

            View view = this.getCurrentFocus();

            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }


        }

    }

    private void initPrevFundingLayout(MoimingSessionVO sessionVO) {

        int moimingCnt = sessionInvitedMembers.size() + 1; // 현 유저도 넣어야함
        textMemberInviteCnt.setText(moimingCnt + "명");
        textNmuInviteCnt.setText(inviteNmuCnt + "명");

        int totalCnt = moimingCnt + inviteNmuCnt;

        // 설계 필요.
        // 일단 이전 모금 멤버들 그대로 가져오는게 정석.
        // 1. 이전 모금에 (나) 가 포함되어 있을 경우
        // 모으는 totalCost 도 같이 S/U 한다.
        boolean isCurUserPreviousMember = false;

        for (UserSessionLinkerVO sessionLinker : sessionVO.getUsLinkerList()) {
            MoimingUserVO prevMember = sessionLinker.getMoimingUser();

            SessionMemberLinkerData prevMemberData = new SessionMemberLinkerData(prevMember.getUuid()
                    , prevMember.getUserName()
                    , prevMember.getUserPfImg()
                    , sessionLinker.getPersonalCost());

            totalCost += sessionLinker.getPersonalCost(); // 현재 정산 totalCost update
            if (!sessionLinker.getPersonalCost().equals(sessionVO.getSingleCost())) { // 금액이 수정된 상태였다면?
                prevMemberData.setIsEdited(true); // 체크표시 될 수 있도록!
            }

            if (!prevMember.getUuid().equals(curUser.getUuid())) {
                membersSessionDataList.add(prevMemberData);
            } else {
                isCurUserPreviousMember = true;
                membersSessionDataList.add(0, prevMemberData);
            }
        }

        // 2. 이전 모금에 (나) 가 포함되어 있지 않을 경우 --> 전체 금액이 증가하고,
        if (!isCurUserPreviousMember) {
            SessionMemberLinkerData prevMemberData = new SessionMemberLinkerData(curUser.getUuid()
                    , curUser.getUserName(), curUser.getUserPfImg(), sessionVO.getSingleCost());
            membersSessionDataList.add(0, prevMemberData); // 제일 처음에 넣는다.

        }

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


        // TODO: 중복 코드. 맨 아래 작성된  것 활용하면 좋을 듯.
        LinearLayoutManager membersLayoutManager = new LinearLayoutManager(this);

        //1. 모이밍 유저 것 해결.
        memberRecycler.setLayoutManager(membersLayoutManager);
        membersAdapter = new MembersEditPersonalCostRecyclerAdapter(getApplicationContext(), totalCnt, membersSessionDataList, editCostListener, cancelCheckBoxListener, cntListener);
        memberRecycler.setAdapter(membersAdapter);
        memberRecycler.addItemDecoration(new RecyclerViewItemDecoration(this, 8));

        //2. 비모이머것 해결.
        LinearLayoutManager nmuLayoutManager = new LinearLayoutManager(this);

        nmuRecycler.setLayoutManager(nmuLayoutManager);
        nmuAdapter = new NmuEditPersonalCostRecyclerAdapter(getApplicationContext(), totalCnt, nmuSessionDataList, editCostListener, cancelCheckBoxListener, cntListener, nmuNameChangeListener);
        nmuRecycler.setAdapter(nmuAdapter);
        nmuRecycler.addItemDecoration(new RecyclerViewItemDecoration(this, 8));

    }

    private void setReceiptForPrevFunding(int totalMemberCnt) {

        layoutSessionResult.setVisibility(View.VISIBLE);
        btnCreationFinish.setTextColor(getResources().getColor(R.color.moimingTheme, null));
        btnCreationFinish.setEnabled(true);

        String personalCost = inputCost.getText().toString();

        resultTotalCost.setText(String.valueOf(totalCost));
        resultPersonalCost.setText(String.valueOf(personalCost));
        resultMemberCnt.setText(String.valueOf(totalMemberCnt));

    }

    private void setReceipt() {

        layoutSessionResult.setVisibility(View.VISIBLE);
        btnCreationFinish.setTextColor(getResources().getColor(R.color.moimingTheme, null));
        btnCreationFinish.setEnabled(true);

        // 새로 들어온대로 설정.
        // 1. 총 금액 // 2. 총 인원 // 3. 인원별 금액. //4. 수정 사항 발생시 +- 표시
        if (sessionType == 1) {
            resultTotalCost.setText(String.valueOf(totalCost));

            int totalCnt = inviteNmuCnt + sessionInvitedMembers.size() + 1;
            resultMemberCnt.setText(String.valueOf(totalCnt));

            int dividedN = (int) (totalCost / totalCnt);
            resultPersonalCost.setText(String.valueOf(dividedN));

        } else {

            int totalCnt = inviteNmuCnt + sessionInvitedMembers.size() + 1;
            int dividedN = Integer.parseInt(inputCost.getText().toString());
            int fundingTotalCost = totalCnt * dividedN;

            resultTotalCost.setText(String.valueOf(fundingTotalCost));
            resultPersonalCost.setText(String.valueOf(dividedN));
            resultMemberCnt.setText(String.valueOf(totalCnt));


        }
    }

    private void refreshReceiptFundings() {

        int totalMembers = membersSessionDataList.size() + nmuSessionDataList.size();
        int totalFundingCost = totalMembers * fundingCost;

        resultTotalCost.setText(String.valueOf(totalFundingCost));
        resultPersonalCost.setText(String.valueOf(fundingCost));

        findViewById(R.id.view_edited_cost).setVisibility(View.GONE);
    }

    ////////////////////////////// ㅣ게 ㅁ문제
    private void editReceiptFundings(boolean isCheckboxListener, int editedCost, int prevCost) {

//        if (isCheckBoxListener) { // 1. 개인별 금액이 수정되는 경우.
        int diff;

        if (isCheckboxListener) {
            diff = editedCost - prevCost; // +이든 - 이든.
            totalCost += diff;
        } else {

            diff = fundingCost - prevCost;
            totalCost += diff;

        }

        resultTotalCost.setText(String.valueOf(totalCost));
/*
        } else { // 2. 금액 수정이 취소되는 경우.

            int prevEditedCost = editedCost;
            int diff = prevEditedCost - fundingCost;


        }*/

        findViewById(R.id.view_edited_cost).setVisibility(View.VISIBLE);


    }

    private void editReceiptDutchpay(boolean reset, int newDividedN) {

        resultPersonalCost.setText(String.valueOf(newDividedN));

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


    private ConstraintLayout layoutDrawer, layoutPersonalCost;
    private TextView textMemberInviteCnt, textNmuInviteCnt; // x 명
    private RecyclerView memberRecycler, nmuRecycler;
    private TextView btnResetPersonal;
    private View sizeMatcher;

    // 계산서
    private TextView resultMemberCnt, resultTotalCost, resultPersonalCost;

    List<SessionMemberLinkerData> membersSessionDataList;
    List<SessionNmuLinkerData> nmuSessionDataList;

    MembersEditPersonalCostRecyclerAdapter membersAdapter;
    NmuEditPersonalCostRecyclerAdapter nmuAdapter;


    private void initPersonalCostLayout() {

        // 1. 텍스트들 활성화
        int moimingCnt = sessionInvitedMembers.size() + 1; // 현 유저도 넣어야함
        textMemberInviteCnt.setText(moimingCnt + "명");
        textNmuInviteCnt.setText(inviteNmuCnt + "명");

        // onActivityResult 이후
        // 2. 리사이클러 뷰 init
        initRecyclerViews(moimingCnt + inviteNmuCnt);

    }


    private void initRecyclerViews(int totalCnt) {

        buildLists();

        LinearLayoutManager membersLayoutManager = new LinearLayoutManager(this);

        //1. 모이밍 유저 것 해결.
        memberRecycler.setLayoutManager(membersLayoutManager);
        membersAdapter = new MembersEditPersonalCostRecyclerAdapter(getApplicationContext(), totalCnt, membersSessionDataList, editCostListener, cancelCheckBoxListener, cntListener);
        memberRecycler.setAdapter(membersAdapter);
        memberRecycler.addItemDecoration(new RecyclerViewItemDecoration(this, 8));

        //2. 비모이머것 해결.
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
            // 0. 금액을 N 빵한다.
            int invitedCnt = sessionInvitedMembers.size() + inviteNmuCnt + 1;
            dividedN = (int) (totalCost / invitedCnt);
            curUserCost = dividedN;

            if ((dividedN * invitedCnt) != totalCost) {

                int diff = totalCost - (dividedN * invitedCnt);
                curUserCost += diff;
            }

        } else { // 모금일 경우

            dividedN = Integer.parseInt(inputCost.getText().toString());
            curUserCost = dividedN;

        }

        // 1. 해당 총무의 것을 먼저 정한다.
        SessionMemberLinkerData curUserLinkData = new SessionMemberLinkerData(curUser.getUuid(), curUser.getUserName() + " (나)", curUser.getUserPfImg(), curUserCost);
        membersSessionDataList.add(curUserLinkData);

        // 이미 참여하는 GroupMemberDTO list 는 업데이트 된 상태.
        // 2. 다른 유저들을 포함한다.
        for (MoimingMembersDTO invitedMember : sessionInvitedMembers) {

            SessionMemberLinkerData invitedMemberLinkerData = new SessionMemberLinkerData(invitedMember.getUuid(), invitedMember.getUserName(), invitedMember.getUserPfImg(), dividedN);
            membersSessionDataList.add(invitedMemberLinkerData);

        }

        // 3. 비모이밍 유저들의 것도 만들어 준다.
        if (inviteNmuCnt != 0) {
            for (int i = 1; i < inviteNmuCnt + 1; i++) {

                String userName = "친구 " + i;
                SessionNmuLinkerData invitedNmuLinkerData = new SessionNmuLinkerData(userName, dividedN);
                nmuSessionDataList.add(invitedNmuLinkerData);
            }
        }
    }

    public UserCostCancelCheckBoxListener cancelCheckBoxListener = new UserCostCancelCheckBoxListener() { // 지금 이상함.
        @Override
        public void onCancelCheckbox(boolean isMoimingUser, int position, int prevFundingCost) {
            if (sessionType == 1) {
                int totalCostCal = totalCost; // 정보도 있음.
                int totalInvitedUsers = membersSessionDataList.size() + nmuSessionDataList.size();
                int newDividedN;
                int left = 0;

                // 지금까지 선택된 녀석들의 값을 빼면서 다시 N빵을 친다.

                if (isMoimingUser) {

                    SessionMemberLinkerData uncheckedData = membersSessionDataList.get(position);
                    uncheckedData.setIsEdited(false);

                    membersSessionDataList.set(position, uncheckedData);

                    for (int i = 0; i < membersSessionDataList.size(); i++) { // 돌면서 Check 된 애들의 값과 인원을 제외한다.

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


                    newDividedN = (int) (totalCostCal / totalInvitedUsers); // 반영된 애들끼리 새로 N

                    if ((newDividedN * totalInvitedUsers) != totalCostCal) {
                        left = totalCostCal - (newDividedN * totalInvitedUsers); // 현재 포지션인 리더인 친구한테 차액 부과 적용
                    }

                    // N빵을 나머지 친구들한테 적용.
                    for (int i = 0; i < membersSessionDataList.size(); i++) {

                        SessionMemberLinkerData member = membersSessionDataList.get(i);

                        if (member.getIsEdited()) { // 금액 변경한 친구?

                            if (i == 0) {
                                int curUserCost = member.getUserCost();
                                member.setUserCost(curUserCost + left);
                            }

                           /* if (i == position) {
                                member.setUserCost(newDividedN);
                                member.setIsEdited(false);
                            }*/
                        } else { // N빵 적용 대상 친구들
                            if (i == 0) {
                                member.setUserCost(newDividedN + left);
                            }

                            member.setUserCost(newDividedN);
                        }

                        membersSessionDataList.set(i, member);
                    }

                    for (int i = 0; i < nmuSessionDataList.size(); i++) {

                        SessionNmuLinkerData nmu = nmuSessionDataList.get(i);

                        if (!nmu.getIsEdited()) { // 계속 돌리는데, edit 된 상태가 아닌 친구

                            nmu.setUserCost(newDividedN);
                        }

                        nmuSessionDataList.set(i, nmu);
                    }

                } else { // NMU 를 변경하였을 경우!

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
                        left = totalCostCal - (newDividedN * totalInvitedUsers); // 현재 포지션인 리더인 친구한테 차액 부과 적용
                    }

                    for (int i = 0; i < membersSessionDataList.size(); i++) {

                        SessionMemberLinkerData member = membersSessionDataList.get(i);

                        if (member.getIsEdited()) {
                            // 냅둡니다.
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

            } else { // 모금일 경우

                // 원래 모금 가격은 적혀있는 가격임.
                if (isMoimingUser) {

                    SessionMemberLinkerData userData = membersSessionDataList.get(position);
                    userData.setUserCost(fundingCost); // 현재 fundingCost 로 바꿔준다.
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

            if (sessionType == 1) { // 더치페이일 경우
                int totalCostCal = totalCost; // 정보도 있음.
                int totalInvitedUsers = membersSessionDataList.size() + nmuSessionDataList.size();
                int newDividedN;
                int left = 0;

                // 체크박스 후 수정 완료하면 들어오게 됨.
                // 1. 해당 유저의 정보를 변경한다.
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
                        // 체크를 해제한다

                        Toast.makeText(getApplicationContext(), "조정 가격 합이 총 금액을 초과합니다.", Toast.LENGTH_SHORT).show();

                        userData.setUserCost(prevUserCost);
                        userData.setIsEdited(false);


                        membersAdapter.notifyDataSetChanged();
                        return;

                    }


                    if (totalInvitedUsers == 0) { // 금지해놔서 안들어올듯
                        // 다 각자 설정된 금액이 있는 것. left 만 총무에게 더해주면 됨.
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
                        // 지금 바뀌는 놈이 총무일 경우?
                        SessionMemberLinkerData member = membersSessionDataList.get(i);

                        if (member.getIsEdited()) {
                            // 냅둡니다.
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


                } else { // 수정하는게 비모이밍 유저이다.

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
                        // 체크를 해제한다

                        Toast.makeText(getApplicationContext(), "조정 가격 합이 총 금액을 초과합니다.", Toast.LENGTH_SHORT).show();

                        nmuData.setUserCost(prevUserCost);
                        nmuData.setIsEdited(false);

                        nmuAdapter.notifyDataSetChanged();

                        return;

                    }

                    if (totalInvitedUsers == 0) {
                        // 다 각자 설정된 금액이 있는 것. left 만 총무에게 더해주면 됨.

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
                        // 지금 바뀌는 놈이 총무일 경우?
                        SessionMemberLinkerData member = membersSessionDataList.get(i);

                        if (member.getIsEdited()) {
                            // 냅둡니다.
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

            } else { // 모금일 경우: 누구의 금액을 수정했을 경우, 그 사람것만 바꿔주면 된다. position, personalCost

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

                s += "[번호" + i + "]"
                        + "\n이름: " + data.getUserName()
                        + "\n가격: " + String.valueOf(data.getUserCost())
                        + "\n체크여부: " + String.valueOf(data.getIsEdited())
                        + "\n";
            }

            Log.w("TAG", s);

            membersAdapter.notifyDataSetChanged();
            nmuAdapter.notifyDataSetChanged();
        }
    };


    public void scrollToView(View view, final ScrollView scrollView, int count) {
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


    // TODO : 그래서 결론적으로 만드는 부분 singleCost 잘 넣어야 함.
    //       이후 prevFunding 도 반영하고 생성하는것까지 하면 될듯.
    //       추후 사용될 수 있는 곳 발견하면 바꾸기. (prevFunding 이후 키보드 올라오는거 제어좀;)

    private MoimingSessionRequestDTO makeSessionRequestData() {

        // 1. Session 정보 확인.
        String sessionName = inputSessionName.getText().toString();

        int sessionMemberCnt = membersSessionDataList.size() + nmuSessionDataList.size();
        int totalSessionCost;
        int singleCost;
        if (sessionType == 1) {
            totalSessionCost = Integer.parseInt(inputCost.getText().toString());
            // 조정 금액들을 포함해서 n빵 금액 저장
            singleCost = Integer.parseInt(resultPersonalCost.getText().toString());
        } else {
            totalSessionCost = totalCost;
            // 적혀있는 값이 정규!
            singleCost = Integer.parseInt(inputCost.getText().toString());
        }
        if (sessionName.isEmpty() || sessionMemberCnt == 0 || totalCost == 0) {
            Toast.makeText(getApplicationContext(), "제대로된 데이터를 형성할 수 없습니다. 입력을 확인해 주세요", Toast.LENGTH_SHORT).show();
            return null;
        }

        Integer creatorCost = membersSessionDataList.get(0).getUserCost();

        if (fromNewGroupCreation) { // Creator Cost 는 총무는 돈을 보냈다고 고려하기 때문에 초반 금액을 설정해주기 위해 보낸다.

            return new MoimingSessionRequestDTO(curUser.getUuid(), null, sessionName, creatorCost, sessionType, sessionMemberCnt, totalSessionCost, singleCost);

        } else {

            return new MoimingSessionRequestDTO(curUser.getUuid(), curGroup.getUuid(), sessionName, creatorCost, sessionType, sessionMemberCnt, totalSessionCost, singleCost);

        }


    }

    private void requestSessionBuild() {

        // 보낼 데이터를 먼저 만든 후에, 점검 후 보내자. 1// SessionRequestDTO
        MoimingSessionRequestDTO sessionRequestDTO = makeSessionRequestData();

        if (sessionRequestDTO != null) {

            TransferModel<MoimingSessionRequestDTO> transferModel = new TransferModel<>(sessionRequestDTO);
            SessionRetrofitService sessionRetrofitService = GlobalRetrofit.getInstance().getRetrofit().create(SessionRetrofitService.class);

            sessionRetrofitService.sessionCreationRequest(transferModel)
                    .subscribeOn(Schedulers.io()) // 새로운 스레드에서 진행된다.
                    .observeOn(AndroidSchedulers.mainThread()) // SubscribeOn 은 UI update 하기 위해 대기하는 것
                    .subscribe(new Observer<TransferModel<MoimingSessionResponseDTO>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull TransferModel<MoimingSessionResponseDTO> responseDTO) {

                            // 저쪽에서 준 데이터 확인
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

            Toast.makeText(getApplicationContext(), "제대로된 데이터를 형성할 수 없습니다. 입력을 확인해 주세요", Toast.LENGTH_SHORT).show();

            return;

        }
        // 1. Session 을 먼저 만든다. // 만든 것을 완성후 세션 정보를 한번 가져와줘야 함.

        // 2. 각 유저들의 SessionLinker 들을 만들어 주고 List 로 합쳐준다 .

        // 3. User Linker Request 를 보낸다. (비모이밍 유저 것도 한거번에 전달)

        // Session Linker Requset DTO 들 포함 내용.

        // 1. isMoimingUser /

    }

    private void makeLinkerRequest() {

        // 2. USLinker 정보 확인.
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


        // Linker Request 를 보낸다.
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

                        Toast.makeText(getApplicationContext(), "아마 생성 성공", Toast.LENGTH_SHORT).show();

                        // 새로운 세션을 생성한 경우 // 해당 액티비티로 돌아갈 경우 초기화를 담당한다.
                        // 바로 생성일 경우 초기화 안해도 됨.
                        if (fromGroupActivity) GroupActivity.SESSION_LIST_REFRESH_FLAG = true;


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                        Intent toSession = new Intent(SessionCreationActivity.this, SessionActivity.class);

                        toSession.putExtra("cur_user_status", 0); // 내가 만든 사람임
                        toSession.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                        toSession.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                        toSession.putExtra(GroupActivity.MOIMING_SESSION_DATA_KEY, createdSession);

                        startActivity(toSession);

                        finish();
                    }
                });
    }
}