package com.example.moimingrelease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_adapter.GroupSessionViewAdapter;
import com.example.moimingrelease.app_listener_interface.GroupExitDialogListener;
import com.example.moimingrelease.fragments_main.MoimingHomeFragment;
import com.example.moimingrelease.moiming_model.dialog.AppProcessDialog;
import com.example.moimingrelease.moiming_model.dialog.GroupExitConfirmDialog;
import com.example.moimingrelease.moiming_model.dialog.GroupExitInGroupDialog;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.extras.SessionAndUserStatusDTO;
import com.example.moimingrelease.moiming_model.extras.UserGroupUuidDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.dialog.CreateSessionDialog;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupRetrofitService;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.UGLinkerRetrofitService;
import com.example.moimingrelease.network.fcm.FCMRequest;
import com.example.moimingrelease.network.kakao_api.KakaoMoimingFriends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GroupActivity extends AppCompatActivity {

    public final String GROUP_TAG = "GROUP_TAG";
    public static final String MOIMING_SESSION_DATA_KEY = "current_moiming_session";
    public static final String MOIMING_GROUP_DATA_KEY = "current_moiming_group";
    public static final String MOIMING_GROUP_MEMBERS_KEY = "current_group_member";
    public static boolean SESSION_LIST_REFRESH_FLAG = false;
    public static boolean GROUP_INFO_REFRESH_FLAG = false;

    private FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    KakaoMoimingFriends kmf = KakaoMoimingFriends.getInstance();

    private Map<UUID, String> groupMemberFcmTokenMap;

    public List<MoimingMembersDTO> groupMembers = new ArrayList<>();
    public List<SessionAndUserStatusDTO> sessionAdapterData;
    private List<SessionAndUserStatusDTO> sessionRawDataHolder;
    private List<String> groupMembersUuid;

    MoimingGroupVO curGroup;
    MoimingUserVO curUser;

    // Functions
    private CreateSessionDialog createSessionDialog;
    private AppProcessDialog processDialog;

    // Views
//    private ScrollView groupScroll;
    private SlidingUpPanelLayout slidingLayout;
    private ImageView btnFinish, btnPayment, btnGroupMore, btnGroupInfo, btnEditBgImg, btnInviteMembers, imgGroupBg;
    private TextView textGroupName, textNoResult;
    private LinearLayout btnCreateSession;
    private RadioGroup sessionFilter;
    private RadioButton filterEvery, filterDutchpay, filterFunding, filterMySession;
    private boolean isFiltered = false;
    private RecyclerView groupSessionViews;
    private GroupSessionViewAdapter sessionViewsAdapter;
    private ConstraintLayout btnGroupNotice;
    private TextView textGroupNotice, textNoticeFix;

    // Group Members
    private ConstraintLayout layoutMembersView;

    //Sliding Panel Layout Views
//    private ConstraintLayout drawerLayout;
    private RelativeLayout drawerLayout;
    private EditText searchSession;

    GroupExitDialogListener exitDialogListener = new GroupExitDialogListener() {
        @Override
        public void initExitConfirmDialog(boolean isConfirmed, String groupUuid) {

            // 다이얼로그 소환!
            if (!isConfirmed) {

                GroupExitConfirmDialog exitDialog = new GroupExitConfirmDialog(GroupActivity.this, this, groupUuid);
                exitDialog.show();

            } else { // 위에 다이얼로그에서 확인해서 진짜 제거하는 부분.


                UGLinkerRetrofitService retrofit = GlobalRetrofit.getInstance().getRetrofit().create(UGLinkerRetrofitService.class);
                UserGroupUuidDTO sendChunk = new UserGroupUuidDTO(curUser.getUuid(), UUID.fromString(groupUuid));
                TransferModel<UserGroupUuidDTO> requestModel = new TransferModel<>(sendChunk);

                retrofit.deleteGroupFromUser(requestModel)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<TransferModel>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull TransferModel transferModel) {

                                int num = transferModel.getResultCode();

                                if (num != 500) {
                                    Toast.makeText(getApplicationContext(), "제거되었습니다.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e.getMessage() != null) {
                                    Log.e(MainActivity.MAIN_TAG, e.getMessage());
                                }
                            }

                            @Override
                            public void onComplete() {

                                // REFERSH 가 필요.
                                // 1. rawData 를 다시 받아오고,
                                finish(); // Finish 후 해당 그룹을 나가게 된다.
                            }
                        });

            }
        }
    };


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = searchSession.getText().toString();
            searchSessions(text);
        }
    };


    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // 현재 List 를 가져온다
            switch (checkedId) {

                case R.id.filter_every:
                    isFiltered = false;
                    filterSessions(0);
                    break;

                case R.id.filter_dutchpay:
                    isFiltered = true;
                    filterSessions(1);
                    break;

                case R.id.filter_funding:
                    isFiltered = true;
                    filterSessions(2);
                    break;

                case R.id.filter_my_session:
                    isFiltered = true;
                    filterSessions(3);
                    break;
            }
        }
    };

    private List<SessionAndUserStatusDTO> sessionFilteredAdapterData;


    private void filterSessions(int index) {

        /**
         * Filter Index
         * 0: 전체 보여주는 필터 (Base 를 다 보여주면 된다)
         * 1: 더치페이 필터 (sessionType dutchpay 로 파싱)
         * 2: 모금 필터 (sessionType funding 으로 파싱)
         * 3: 현 유저가 참여한 정산 필터 (curUserStatus 로 파싱)
         */

        if (sessionAdapterData.size() == 0) return;

        // 검색이 되었든, 안되었든 일단 현재 보여지고 있는 sessionAdapterData 를 베이스로 따로 빼낼 것임.
        sessionFilteredAdapterData.clear();

        if (index == 0) {

            // 베이스로 돌려 놓는다.

            sortSessionList(sessionAdapterData);
            sessionViewsAdapter.setSessionList(sessionAdapterData);
            sessionViewsAdapter.notifyDataSetChanged();


        } else { // 전체 필터가 아닐 경우

            if (index == 1) { // 더치페이 필터

                for (int i = 0; i < sessionAdapterData.size(); i++) {
                    SessionAndUserStatusDTO sessionData = sessionAdapterData.get(i);
                    if (sessionData.getMoimingSessionResponseDTO().getSessionType() == 1) {

                        sessionFilteredAdapterData.add(sessionData);
                    }
                }

            } else if (index == 2) { // 모금 필터

                for (int i = 0; i < sessionAdapterData.size(); i++) {
                    SessionAndUserStatusDTO sessionData = sessionAdapterData.get(i);
                    if (sessionData.getMoimingSessionResponseDTO().getSessionType() == 0) {

                        sessionFilteredAdapterData.add(sessionData);
                    }
                }

            } else { // 현 유저 참여 필터

                for (int i = 0; i < sessionAdapterData.size(); i++) {
                    SessionAndUserStatusDTO sessionData = sessionAdapterData.get(i);
                    // 내가 참여하지 않는것만 아니면 되는듯?
                    if (sessionData.getCurUserStatus() != 4) {

                        sessionFilteredAdapterData.add(sessionData);
                    }
                }
            }

            sortSessionList(sessionFilteredAdapterData);
            sessionViewsAdapter.setSessionList(sessionFilteredAdapterData);
            sessionViewsAdapter.notifyDataSetChanged();
        }

//        groupSessionViews.scrollToPosition(0);

    }

    private void sendInvitedUserFcm(String fcmToken) throws JSONException {

        String textNoti = curUser.getUserName() + "님이 회원님을 " + curGroup.getGroupName() + "에 초대하셨습니다. 같이 즐거운 모임 만들어 나가세요!";

        JSONObject jsonSend = FCMRequest.getInstance().buildFcmJsonData("group", String.valueOf(1), "새로운 모임"
                , textNoti, "", curGroup.getUuid().toString(), "", fcmToken);

        RequestBody reBody = RequestBody.create(MediaType.parse("application/json, charset-utf8")
                , String.valueOf(jsonSend));

        FCMRequest.getInstance().sendFcmRequest(getApplicationContext(), reBody);

    }


    // 처음에 가져올 경우
    private void receiveFcmTokens(List<String> addingMemberUuid, boolean isMemberAdded) {

        for (int i = 0; i < addingMemberUuid.size(); i++) {

            DocumentReference keyDocs = firebaseDB.collection("UserInfo")
                    .document(addingMemberUuid.get(i)); // uuid 가 문서 명임.


            keyDocs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull @NotNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        DocumentSnapshot snapshot = task.getResult();

                        String userUuid = snapshot.getId();

                        if (snapshot.exists()) {

                            Map<String, Object> responseSnap = snapshot.getData();
                            String memberFcmToken = (String) responseSnap.get("fcm_token");

                            groupMemberFcmTokenMap.put(UUID.fromString(userUuid), memberFcmToken);

                            if (isMemberAdded || isCreatedWithSession) { // 멤버 추가하고 돌아왔거나, 정산활동 동시생성 중이거나

                                try {

                                    sendInvitedUserFcm(memberFcmToken);

                                } catch (JSONException e) {

                                    if (e.getMessage() != null) {

                                        Log.e("FCM_ERRR", e.getMessage());

                                    }
                                }
                            }


                        } else {

                            Log.e("FCM_TAG", "해당 유저의 토큰 정보가 없습니다");
                        }

                    } else {
                        Log.e("FCM_TAG", "해당 문서를 불러오지 못했습니다");
                    }
                }
            });

        }


    }

    private View heightLocator;
    private boolean isPanelHeightSet = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!isPanelHeightSet) {

            setPanelHeight();
        }

    }

    private void setPanelHeight() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int getDeviceHeightPx = displayMetrics.heightPixels;

        int[] location = new int[2];
        heightLocator.getLocationOnScreen(location);
        int locatorHeightPx = location[1];

        int panelHeightPx = getDeviceHeightPx - locatorHeightPx;

        slidingLayout.setPanelHeight(panelHeightPx);

        isPanelHeightSet = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        receiveIntent();

        initView();

        initParams();

        receiveFcmTokens(groupMembersUuid, false);

        layoutMembersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnGroupNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toNotice = new Intent(GroupActivity.this, GroupNoticeActivity.class);

                toNotice.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                toNotice.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                toNotice.putParcelableArrayListExtra(getResources().getString(R.string.group_members_data_key)
                        , (ArrayList<MoimingMembersDTO>) groupMembers);

                startActivityForResult(toNotice, 101);
            }
        });


        // 처음 Focus 를 얻을때도 동일한 작동 필요 (Sliding Panel 이 올라온 상태)
        searchSession.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });


        // 이후 Focus 가 지속되는 동안 다시 눌러도 동일한 작동 필요
        searchSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        btnCreateSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createSessionDialog.setCurUser(curUser);
                createSessionDialog.setCurGroup(curGroup);
                createSessionDialog.setGroupMembers(groupMembers);

                createSession();
            }
        });


        btnInviteMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent inviteMembersActivity = new Intent(GroupActivity.this, InviteGroupMembersActivity.class);

                inviteMembersActivity.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                inviteMembersActivity.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                inviteMembersActivity.putStringArrayListExtra("group_members_uuid", (ArrayList<String>) groupMembersUuid);
                inviteMembersActivity.putExtra(getResources().getString(R.string.fcm_token_map), (Serializable) groupMemberFcmTokenMap);

                startActivityForResult(inviteMembersActivity, 100);
            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toGroupPaymentActivity = new Intent(GroupActivity.this, GroupPaymentActivity.class);

                toGroupPaymentActivity.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                toGroupPaymentActivity.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);

//                toGroupPaymentActivity.putExtra("group_uuid", curGroup.getUuid().toString());
                toGroupPaymentActivity.putExtra(getResources().getString(R.string.fcm_token_map), (Serializable) groupMemberFcmTokenMap);

                startActivity(toGroupPaymentActivity);
            }
        });

        btnGroupMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: AlertDialog 위치 조정하기. 해당 View 와 연동해서 코딩하기. , 그냥 정수를 넣어도 뭔가 잘 안되는 듯.

                GroupExitInGroupDialog exitDialog = new GroupExitInGroupDialog(GroupActivity.this, exitDialogListener, curGroup.getUuid().toString());
//
//                WindowManager.LayoutParams params = exitDialog.getWindow().getAttributes();
//
//                params.x = Math.round(v.getX());
//                params.y = Math.round(v.getY());
//
//                params.x = 100;
//                params.y = 10;
//                exitDialog.getWindow().setAttributes(params);

                exitDialog.show();
            }
        });

        layoutMembersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent inviteMembersActivity = new Intent(GroupActivity.this, GroupMembersViewActivity.class);

                inviteMembersActivity.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                inviteMembersActivity.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                inviteMembersActivity.putParcelableArrayListExtra(getResources().getString(R.string.group_members_data_key), (ArrayList<MoimingMembersDTO>) groupMembers);
                inviteMembersActivity.putStringArrayListExtra("group_members_uuid", (ArrayList<String>) groupMembersUuid);
                inviteMembersActivity.putExtra(getResources().getString(R.string.fcm_token_map), (Serializable) groupMemberFcmTokenMap);


                startActivityForResult(inviteMembersActivity, 102); // 해당 그룹의 uuid 전달 필요
            }
        });

        btnGroupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewInfoActivity = new Intent(GroupActivity.this, GroupInfoActivity.class);

                int sessionCnt = sessionRawDataHolder.size();

                viewInfoActivity.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                viewInfoActivity.putExtra("group_session_cnt", sessionCnt);

                startActivityForResult(viewInfoActivity, 103);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

//        getGroupMembers();
        getGroupSessions();

    }


    private void initView() {

//        groupScroll = findViewById(R.id.noti_main_scroll);

        heightLocator = findViewById(R.id.view_height_locator);

        textGroupName = findViewById(R.id.text_group_name);
        textGroupName.setText(curGroup.getGroupName());

        btnGroupInfo = findViewById(R.id.btn_group_info);
        btnCreateSession = findViewById(R.id.btn_create_session);
        btnInviteMembers = findViewById(R.id.btn_invite_members);
        groupSessionViews = findViewById(R.id.session_recycler_view);
        btnPayment = findViewById(R.id.btn_to_payment);
        btnFinish = findViewById(R.id.btn_finish_group_activity);
        btnGroupMore = findViewById(R.id.btn_group_more);

        // Sliding Panel
        slidingLayout = findViewById(R.id.layout_sliding);
        drawerLayout = findViewById(R.id.layout_drawer_session);
        searchSession = findViewById(R.id.input_session_search);
        searchSession.addTextChangedListener(textWatcher);
        textNoResult = findViewById(R.id.text_session_no_result);

        layoutMembersView = findViewById(R.id.layout_group_members_view);

        // filtering buttons
        filterEvery = findViewById(R.id.filter_every);
        filterEvery.setChecked(true); // 처음에는 전체 데이터를 표시하게 되어있다.
        filterDutchpay = findViewById(R.id.filter_dutchpay);
        filterFunding = findViewById(R.id.filter_funding);
        filterMySession = findViewById(R.id.filter_my_session);
        sessionFilter = findViewById(R.id.session_filter);
        sessionFilter.setOnCheckedChangeListener(checkedChangeListener);

        // Group Notice
        btnGroupNotice = findViewById(R.id.layout_group_notice);
        textGroupNotice = findViewById(R.id.text_group_notice);
        textNoticeFix = findViewById(R.id.text_notice_fix);

        setNoticeView();
    }

    private void initParams() {

        createSessionDialog = new CreateSessionDialog(GroupActivity.this);
        processDialog = new AppProcessDialog(GroupActivity.this);

        sessionAdapterData = new ArrayList<>();
        sessionRawDataHolder = new ArrayList<>();
        sessionFilteredAdapterData = new ArrayList<>();

        groupMemberFcmTokenMap = new HashMap<>();

        groupMembersUuid = new ArrayList<>(); // 아답터 전송 용

        for (int i = 0; i < groupMembers.size(); i++) {

            groupMembersUuid.add((groupMembers.get(i)).getUuid().toString());

        }

    }

    private void setNoticeView() {

        if (curGroup.getNotice() != null) { // 존재함.

            textNoticeFix.setVisibility(View.VISIBLE);

            String noticeInfo = curGroup.getNotice();

            if (noticeInfo.length() > 29) {

                noticeInfo = curGroup.getNotice().subSequence(0, 29) + "...";
            }

            textGroupNotice.setText(noticeInfo);

        } else {

            textNoticeFix.setVisibility(View.GONE);
            textGroupNotice.setText("모임원들에게 공지할 내용을 입력해주세요");
            textGroupNotice.setTextColor(getResources().getColor(R.color.moimingTheme, null));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 || requestCode == 102) { // 두 경로로 초대
            if (resultCode == RESULT_OK) {

                if (data != null) { // 멤버가 추가될때마다 FCM Map 에 추가된다.
                    List<String> addedUser = data.getStringArrayListExtra("added_user_uuid");
                    receiveFcmTokens(addedUser, true);

                }
            }
        } else if (requestCode == 101) {
            if (resultCode == RESULT_OK) { // Notice 바꿨을 떄 curGroup만 바꿔주고 Notice 란을 바꿔준다.

                curGroup = (MoimingGroupVO) data.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));

                setNoticeView();
            }
        } else if( requestCode == 103){ // 수정 정보 받아들인다.
            if (resultCode == RESULT_OK){

                curGroup = (MoimingGroupVO) data.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));

                textGroupName.setText(curGroup.getGroupName());

            }


        }
    }

    private String movingSessionUuid;
    private boolean isCreatedWithSession;

    private void receiveIntent() {

        Intent dataIntent = getIntent();

        curUser = (MoimingUserVO) dataIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));

        MoimingGroupAndMembersDTO groupAndMembersData = dataIntent.getExtras().getParcelable(getResources().getString(R.string.moiming_group_and_members_data_key));
        if (groupAndMembersData.getMoimingGroup() != null) {
            curGroup = groupAndMembersData.getMoimingGroup();
        } else {
            curGroup = groupAndMembersData.getMoimingGroupDto().convertToVO();
        }


        List<MoimingMembersDTO> rawData = groupAndMembersData.getMoimingMembersList();
        for (MoimingMembersDTO members : rawData) {
            if (!members.getUuid().toString().equals(curUser.getUuid().toString())) {

                groupMembers.add(members);

            } // 나는 뺀다.
        }

        movingSessionUuid = dataIntent.getExtras().getString(getResources().getString(R.string.group_move_to_session_key), "");
        isCreatedWithSession = dataIntent.getExtras().getBoolean("created_with_session", false);


    }


    // TODO: 세션들 검색 후 리사이클러 계속 바꿔주는 코드 필요
    private void searchSessions(@NotNull String searchText) {

        // 1. 세션 리스트 클리어
        sessionAdapterData.clear();

        // 2. 검색
        if (searchText.length() == 0) {

            sessionAdapterData.addAll(sessionRawDataHolder);
            groupSessionViews.setVisibility(View.VISIBLE);
            textNoResult.setVisibility(View.GONE);
            sortSessionList(sessionAdapterData);


        } else {

            // 정산 활동 이름으로 검색되므로, 이름만 Searching 하면 된다.
            for (int i = 0; i < sessionRawDataHolder.size(); i++) {

                SessionAndUserStatusDTO sessionData = sessionRawDataHolder.get(i);

                if (sessionData.getMoimingSessionResponseDTO().getSessionName().toLowerCase().contains(searchText)) {
                    sessionAdapterData.add(sessionData);
                }
            }

            if (sessionAdapterData.size() == 0) {
                groupSessionViews.setVisibility(View.GONE);
                textNoResult.setVisibility(View.VISIBLE);
            } else {
                groupSessionViews.setVisibility(View.VISIBLE);
                textNoResult.setVisibility(View.GONE);
            }

        }

        if (isFiltered) {
            int id = sessionFilter.getCheckedRadioButtonId();
            switch (id) {
                case R.id.filter_dutchpay:
                    filterSessions(1);
                    break;
                case R.id.filter_funding:
                    filterSessions(2);
                    break;
                case R.id.filter_my_session:
                    filterSessions(3);
                    break;

            }

        } else {
            sessionViewsAdapter.notifyDataSetChanged();
        }

    }


    private void createSession() { // 정규 방법으로 정산 활동 만들기.

        createSessionDialog.show();

    }

    private void getRefreshedGroupInfos() {

        processDialog.show();

        GroupRetrofitService groupRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupRetrofitService.class);

        groupRetrofit.getGroupAndMembers(curGroup.getUuid().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<MoimingGroupAndMembersDTO>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<MoimingGroupAndMembersDTO> responseModel) {

                        MoimingGroupAndMembersDTO received = responseModel.getData();

                        curGroup = received.getMoimingGroupDto().convertToVO();

                        groupMembers.clear();

                        List<MoimingMembersDTO> rawData = received.getMoimingMembersList();

                        for (MoimingMembersDTO members : rawData) {

                            if (!members.getUuid().toString().equals(curUser.getUuid().toString())) {

                                groupMembers.add(members);

                            } // 나는 뺀다.
                        }

                        groupMembersUuid.clear(); // 아답터 전송 용도 같이 Refresh

                        for (int i = 0; i < groupMembers.size(); i++) {

                            groupMembersUuid.add((groupMembers.get(i)).getUuid().toString());

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            Log.e(GROUP_TAG, e.getMessage());
                        }

                    }

                    @Override
                    public void onComplete() {

                        Toast.makeText(getApplicationContext(), "그룹 정보 Refresh", Toast.LENGTH_SHORT);

                        GROUP_INFO_REFRESH_FLAG = false;
                        MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = true;

                        processDialog.finish();
                    }
                });
    }


    Comparator<SessionAndUserStatusDTO> byDate = new Comparator<SessionAndUserStatusDTO>() {
        @Override
        public int compare(SessionAndUserStatusDTO dto1, SessionAndUserStatusDTO dto2) {
            return dto2.getMoimingSessionResponseDTO().convertToVO().getCreatedAt().compareTo(dto1.getMoimingSessionResponseDTO().convertToVO().getCreatedAt());
        }
    };


    // TODO: 여기에 넣고 Return 하는 값이 Sorting 된 것이면 사용하기 간편할 듯!
    private void sortSessionList(List<SessionAndUserStatusDTO> preSortingList) {

        Collections.sort(preSortingList, byDate);


    }


    private void getGroupSessions() {

        processDialog.show();

        GroupRetrofitService groupRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupRetrofitService.class);

        int notificationCheck = 0;

        // TODO: 이거 notification check 를 해라 말라 판단하는 기준인데, 이거 좀 이상한듯! 왜 session_list_refresh_flag 가 기준?
        if (!SESSION_LIST_REFRESH_FLAG) notificationCheck = 1;
        else notificationCheck = 0;

        groupRetrofit.requestGroupSessions(curGroup.getUuid().toString(), curUser.getUuid().toString(), notificationCheck)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<SessionAndUserStatusDTO>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<SessionAndUserStatusDTO>> listModel) {

                        sessionRawDataHolder = listModel.getData(); // 해당 데이터는 총 데이터를 집합 시켜놓는 곳.
                        sessionAdapterData.addAll(sessionRawDataHolder); // 저장해 놓는다. //TODO: 여기서 냅다 박으면 안되고 SORTING 필요!

                        sortSessionList(sessionAdapterData);

                        if (sessionRawDataHolder.size() == 0) {

                            Toast.makeText(getApplicationContext(), "정산활동을 시작해보세요 :)", Toast.LENGTH_SHORT).show();

                            groupSessionViews.setVisibility(View.GONE);
                            textNoResult.setVisibility(View.VISIBLE);

                        } else {

                            groupSessionViews.setVisibility(View.VISIBLE);
                            textNoResult.setVisibility(View.GONE);
                        }

                        // NOTIFICATION 변경 여부
                        if (listModel.getDescription() != null) {
                            if (listModel.getDescription().equals("changed")) {
                                MainActivity.IS_NOTIFICATION_REFRESH_NEEDED = true;
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        Log.e(GROUP_TAG, e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                        // 정렬을 시행한다음에 뿌려주면 되는데, 이 때,
                        // sortGroupSessionList();
                        if (!SESSION_LIST_REFRESH_FLAG) { // 초기화가 아닌 경우. (그냥 액티비티 들어왔을 떄)
                            initRecyclerView();
//                            groupSessionViews.scrollToPosition(0);


                        } else { // 재활용인 경우

                            Toast.makeText(getApplicationContext(), "세션 리스트를 재생성합니다아", Toast.LENGTH_SHORT).show();

                            sessionViewsAdapter.setSessionList(sessionAdapterData);
                            sessionViewsAdapter.notifyDataSetChanged();
                            SESSION_LIST_REFRESH_FLAG = false;
                        }

                        if (!movingSessionUuid.equals("")) {

                            SessionAndUserStatusDTO curMovingSession = null;

                            //TODO: 해당 세션을 찾고 해당 세션으로 이동시켜야 한다.
                            for (int i = 0; i < sessionAdapterData.size(); i++) {

                                if (sessionAdapterData.get(i).getMoimingSessionResponseDTO().getUuid()
                                        .toString().equals(movingSessionUuid)) {
                                    curMovingSession = sessionAdapterData.get(i);
                                }

                            }

                            if (curMovingSession != null) {
                                Intent toSession = new Intent(GroupActivity.this, SessionActivity.class);

                                toSession.putExtra("cur_user_status", curMovingSession.getCurUserStatus());
                                toSession.putExtra(getResources().getString(R.string.moiming_user_data_key), curUser);
                                toSession.putExtra(getResources().getString(R.string.moiming_group_data_key), (Serializable) curGroup);
                                toSession.putExtra(GroupActivity.MOIMING_SESSION_DATA_KEY, curMovingSession.getMoimingSessionResponseDTO().convertToVO());

                                startActivity(toSession);

                            } else {

                                Toast.makeText(getApplicationContext(), "조회할 수 없는 정산활동 입니다.", Toast.LENGTH_SHORT).show();
                            }

                            movingSessionUuid = "";
                        }

                        processDialog.finish();
                    }
                });
    }

    private void fillProfilePictures() {


    }


    private void initRecyclerView() {

        // GroupSession 들을 불러온다.
        LinearLayoutManager sessionLayoutManager = new LinearLayoutManager(this);

        groupSessionViews.setLayoutManager(sessionLayoutManager);
        sessionViewsAdapter = new GroupSessionViewAdapter(this, sessionAdapterData, curGroup, curUser); // 아답터 적용 용으로 설정한다.
        groupSessionViews.setAdapter(sessionViewsAdapter);

    }


    // 다른 액티비티 후에 돌아올 경우 onRestart 가 실행된다.
    // onResume 은 항상 실행되는 곳
    @Override
    protected void onRestart() {
        super.onRestart();

        //onResume 에서 List 와 Adapter 를 최신화 시킬 수 있음.
        // FLAG 판단을 통해 정보를 다시 받아온다.
        if (SESSION_LIST_REFRESH_FLAG) { // Refresh 가 필요한 경우

            // 다시 불러올 것이기 때문에 초기화
            sessionRawDataHolder.clear();
            sessionAdapterData.clear();

            getGroupSessions();

        } else if (GROUP_INFO_REFRESH_FLAG) {

            getRefreshedGroupInfos();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        SESSION_LIST_REFRESH_FLAG = false;
        GROUP_INFO_REFRESH_FLAG = false;

    }


}