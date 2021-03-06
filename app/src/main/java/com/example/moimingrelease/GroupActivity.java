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

            // ??????????????? ??????!
            if (!isConfirmed) {

                GroupExitConfirmDialog exitDialog = new GroupExitConfirmDialog(GroupActivity.this, this, groupUuid);
                exitDialog.show();

            } else { // ?????? ????????????????????? ???????????? ?????? ???????????? ??????.


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
                                    Toast.makeText(getApplicationContext(), "?????????????????????.", Toast.LENGTH_SHORT).show();
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

                                // REFERSH ??? ??????.
                                // 1. rawData ??? ?????? ????????????,
                                finish(); // Finish ??? ?????? ????????? ????????? ??????.
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
            // ?????? List ??? ????????????
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
         * 0: ?????? ???????????? ?????? (Base ??? ??? ???????????? ??????)
         * 1: ???????????? ?????? (sessionType dutchpay ??? ??????)
         * 2: ?????? ?????? (sessionType funding ?????? ??????)
         * 3: ??? ????????? ????????? ?????? ?????? (curUserStatus ??? ??????)
         */

        if (sessionAdapterData.size() == 0) return;

        // ????????? ?????????, ???????????? ?????? ?????? ???????????? ?????? sessionAdapterData ??? ???????????? ?????? ?????? ??????.
        sessionFilteredAdapterData.clear();

        if (index == 0) {

            // ???????????? ?????? ?????????.

            sortSessionList(sessionAdapterData);
            sessionViewsAdapter.setSessionList(sessionAdapterData);
            sessionViewsAdapter.notifyDataSetChanged();


        } else { // ?????? ????????? ?????? ??????

            if (index == 1) { // ???????????? ??????

                for (int i = 0; i < sessionAdapterData.size(); i++) {
                    SessionAndUserStatusDTO sessionData = sessionAdapterData.get(i);
                    if (sessionData.getMoimingSessionResponseDTO().getSessionType() == 1) {

                        sessionFilteredAdapterData.add(sessionData);
                    }
                }

            } else if (index == 2) { // ?????? ??????

                for (int i = 0; i < sessionAdapterData.size(); i++) {
                    SessionAndUserStatusDTO sessionData = sessionAdapterData.get(i);
                    if (sessionData.getMoimingSessionResponseDTO().getSessionType() == 0) {

                        sessionFilteredAdapterData.add(sessionData);
                    }
                }

            } else { // ??? ?????? ?????? ??????

                for (int i = 0; i < sessionAdapterData.size(); i++) {
                    SessionAndUserStatusDTO sessionData = sessionAdapterData.get(i);
                    // ?????? ???????????? ???????????? ????????? ??????????
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

        String textNoti = curUser.getUserName() + "?????? ???????????? " + curGroup.getGroupName() + "??? ?????????????????????. ?????? ????????? ?????? ????????? ????????????!";

        JSONObject jsonSend = FCMRequest.getInstance().buildFcmJsonData("group", String.valueOf(1), "????????? ??????"
                , textNoti, "", curGroup.getUuid().toString(), "", fcmToken);

        RequestBody reBody = RequestBody.create(MediaType.parse("application/json, charset-utf8")
                , String.valueOf(jsonSend));

        FCMRequest.getInstance().sendFcmRequest(getApplicationContext(), reBody);

    }


    // ????????? ????????? ??????
    private void receiveFcmTokens(List<String> addingMemberUuid, boolean isMemberAdded) {

        for (int i = 0; i < addingMemberUuid.size(); i++) {

            DocumentReference keyDocs = firebaseDB.collection("UserInfo")
                    .document(addingMemberUuid.get(i)); // uuid ??? ?????? ??????.


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

                            if (isMemberAdded || isCreatedWithSession) { // ?????? ???????????? ???????????????, ???????????? ???????????? ????????????

                                try {

                                    sendInvitedUserFcm(memberFcmToken);

                                } catch (JSONException e) {

                                    if (e.getMessage() != null) {

                                        Log.e("FCM_ERRR", e.getMessage());

                                    }
                                }
                            }


                        } else {

                            Log.e("FCM_TAG", "?????? ????????? ?????? ????????? ????????????");
                        }

                    } else {
                        Log.e("FCM_TAG", "?????? ????????? ???????????? ???????????????");
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


        // ?????? Focus ??? ???????????? ????????? ?????? ?????? (Sliding Panel ??? ????????? ??????)
        searchSession.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });


        // ?????? Focus ??? ???????????? ?????? ?????? ????????? ????????? ?????? ??????
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

                // TODO: AlertDialog ?????? ????????????. ?????? View ??? ???????????? ????????????. , ?????? ????????? ????????? ?????? ??? ????????? ???.

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


                startActivityForResult(inviteMembersActivity, 102); // ?????? ????????? uuid ?????? ??????
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
        filterEvery.setChecked(true); // ???????????? ?????? ???????????? ???????????? ????????????.
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

        groupMembersUuid = new ArrayList<>(); // ????????? ?????? ???

        for (int i = 0; i < groupMembers.size(); i++) {

            groupMembersUuid.add((groupMembers.get(i)).getUuid().toString());

        }

    }

    private void setNoticeView() {

        if (curGroup.getNotice() != null) { // ?????????.

            textNoticeFix.setVisibility(View.VISIBLE);

            String noticeInfo = curGroup.getNotice();

            if (noticeInfo.length() > 29) {

                noticeInfo = curGroup.getNotice().subSequence(0, 29) + "...";
            }

            textGroupNotice.setText(noticeInfo);

        } else {

            textNoticeFix.setVisibility(View.GONE);
            textGroupNotice.setText("?????????????????? ????????? ????????? ??????????????????");
            textGroupNotice.setTextColor(getResources().getColor(R.color.moimingTheme, null));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 || requestCode == 102) { // ??? ????????? ??????
            if (resultCode == RESULT_OK) {

                if (data != null) { // ????????? ?????????????????? FCM Map ??? ????????????.
                    List<String> addedUser = data.getStringArrayListExtra("added_user_uuid");
                    receiveFcmTokens(addedUser, true);

                }
            }
        } else if (requestCode == 101) {
            if (resultCode == RESULT_OK) { // Notice ????????? ??? curGroup??? ???????????? Notice ?????? ????????????.

                curGroup = (MoimingGroupVO) data.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));

                setNoticeView();
            }
        } else if( requestCode == 103){ // ?????? ?????? ???????????????.
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

            } // ?????? ??????.
        }

        movingSessionUuid = dataIntent.getExtras().getString(getResources().getString(R.string.group_move_to_session_key), "");
        isCreatedWithSession = dataIntent.getExtras().getBoolean("created_with_session", false);


    }


    // TODO: ????????? ?????? ??? ??????????????? ?????? ???????????? ?????? ??????
    private void searchSessions(@NotNull String searchText) {

        // 1. ?????? ????????? ?????????
        sessionAdapterData.clear();

        // 2. ??????
        if (searchText.length() == 0) {

            sessionAdapterData.addAll(sessionRawDataHolder);
            groupSessionViews.setVisibility(View.VISIBLE);
            textNoResult.setVisibility(View.GONE);
            sortSessionList(sessionAdapterData);


        } else {

            // ?????? ?????? ???????????? ???????????????, ????????? Searching ?????? ??????.
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


    private void createSession() { // ?????? ???????????? ?????? ?????? ?????????.

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

                            } // ?????? ??????.
                        }

                        groupMembersUuid.clear(); // ????????? ?????? ?????? ?????? Refresh

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

                        Toast.makeText(getApplicationContext(), "?????? ?????? Refresh", Toast.LENGTH_SHORT);

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


    // TODO: ????????? ?????? Return ?????? ?????? Sorting ??? ????????? ???????????? ????????? ???!
    private void sortSessionList(List<SessionAndUserStatusDTO> preSortingList) {

        Collections.sort(preSortingList, byDate);


    }


    private void getGroupSessions() {

        processDialog.show();

        GroupRetrofitService groupRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupRetrofitService.class);

        int notificationCheck = 0;

        // TODO: ?????? notification check ??? ?????? ?????? ???????????? ????????????, ?????? ??? ????????????! ??? session_list_refresh_flag ??? ???????
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

                        sessionRawDataHolder = listModel.getData(); // ?????? ???????????? ??? ???????????? ?????? ???????????? ???.
                        sessionAdapterData.addAll(sessionRawDataHolder); // ????????? ?????????. //TODO: ????????? ?????? ????????? ????????? SORTING ??????!

                        sortSessionList(sessionAdapterData);

                        if (sessionRawDataHolder.size() == 0) {

                            Toast.makeText(getApplicationContext(), "??????????????? ?????????????????? :)", Toast.LENGTH_SHORT).show();

                            groupSessionViews.setVisibility(View.GONE);
                            textNoResult.setVisibility(View.VISIBLE);

                        } else {

                            groupSessionViews.setVisibility(View.VISIBLE);
                            textNoResult.setVisibility(View.GONE);
                        }

                        // NOTIFICATION ?????? ??????
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

                        // ????????? ?????????????????? ???????????? ?????????, ??? ???,
                        // sortGroupSessionList();
                        if (!SESSION_LIST_REFRESH_FLAG) { // ???????????? ?????? ??????. (?????? ???????????? ???????????? ???)
                            initRecyclerView();
//                            groupSessionViews.scrollToPosition(0);


                        } else { // ???????????? ??????

                            Toast.makeText(getApplicationContext(), "?????? ???????????? ?????????????????????", Toast.LENGTH_SHORT).show();

                            sessionViewsAdapter.setSessionList(sessionAdapterData);
                            sessionViewsAdapter.notifyDataSetChanged();
                            SESSION_LIST_REFRESH_FLAG = false;
                        }

                        if (!movingSessionUuid.equals("")) {

                            SessionAndUserStatusDTO curMovingSession = null;

                            //TODO: ?????? ????????? ?????? ?????? ???????????? ??????????????? ??????.
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

                                Toast.makeText(getApplicationContext(), "????????? ??? ?????? ???????????? ?????????.", Toast.LENGTH_SHORT).show();
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

        // GroupSession ?????? ????????????.
        LinearLayoutManager sessionLayoutManager = new LinearLayoutManager(this);

        groupSessionViews.setLayoutManager(sessionLayoutManager);
        sessionViewsAdapter = new GroupSessionViewAdapter(this, sessionAdapterData, curGroup, curUser); // ????????? ?????? ????????? ????????????.
        groupSessionViews.setAdapter(sessionViewsAdapter);

    }


    // ?????? ???????????? ?????? ????????? ?????? onRestart ??? ????????????.
    // onResume ??? ?????? ???????????? ???
    @Override
    protected void onRestart() {
        super.onRestart();

        //onResume ?????? List ??? Adapter ??? ????????? ?????? ??? ??????.
        // FLAG ????????? ?????? ????????? ?????? ????????????.
        if (SESSION_LIST_REFRESH_FLAG) { // Refresh ??? ????????? ??????

            // ?????? ????????? ????????? ????????? ?????????
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