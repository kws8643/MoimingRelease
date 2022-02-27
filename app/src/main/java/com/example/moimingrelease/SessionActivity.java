package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_adapter.SessionConfirmNotificationAdapter;
import com.example.moimingrelease.app_listener_interface.SessionCreatorNotificationCallBack;
import com.example.moimingrelease.app_listener_interface.SessionCreatorNmuCheckboxCallBack;
import com.example.moimingrelease.app_listener_interface.SessionCreatorSentCheckboxCallBack;
import com.example.moimingrelease.app_listener_interface.SessionDialogCallBack;
import com.example.moimingrelease.frag_session.TabSessionFinishedMembers;
import com.example.moimingrelease.frag_session.TabSessionUnFinishedMembers;
import com.example.moimingrelease.moiming_model.dialog.SessionDeleteDialog;
import com.example.moimingrelease.moiming_model.dialog.SessionSettingDialog;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.extras.NotificationUserAndActivityDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.extras.SessionMembersDTO;
import com.example.moimingrelease.moiming_model.extras.SessionStatusChangeDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.UserSessionLinkerVO;
import com.example.moimingrelease.moiming_model.request_dto.NotificationRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingSessionResponseDTO;
import com.example.moimingrelease.moiming_model.response_dto.NonMoimingUserResponseDTO;
import com.example.moimingrelease.moiming_model.response_dto.USLinkerResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.NotificationRetrofitService;
import com.example.moimingrelease.network.SessionRetrofitService;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.USLinkerRetrofitService;
import com.example.moimingrelease.network.fcm.FCMRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SessionActivity extends AppCompatActivity {

    // 여기는 일단 Session UUID 를 가져올 수 있도록 하고, 해당 으로 유저들과 비모이밍 유저들을 가져올 수 있도록 해보자.
    private MoimingUserVO curUser;
    private MoimingGroupVO curGroup;
    private MoimingSessionVO curSession;

    private MoimingMembersDTO creatorInfo;

    // 현재 유저가 어떤 유저인지에 따라 바뀌는 ..
    public int curUserStatus;
    private TextView textMemberFinished; // view_gone
    private ConstraintLayout layoutTotal;
    private ConstraintLayout layoutCreator, layoutMember, layoutSessionStatus;
    private ImageView imgCreator;
    private RecyclerView confirmNotiRecycler;
    private ImageView btnEditSession;

    // 1. 정산 안한 유저
    private LinearLayout btnSendConfirm; // 총무에게 송금 확인 요청을 보낸다.
    private TextView textSendBtn;

    // Fin / Unfin List 두 개 필요
    private FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    public Map<UUID, String> fcmTokenMap = new HashMap<>(); // 총무일 경우 필요
    private String sessionCreatorFcmToken; // 일반 유저일 경우 필요

    public List<UserSessionLinkerVO> finishedMemberLinkerList;
    public List<NonMoimingUserVO> finishedNmuLinkerList;

    // 얘네가 Main List
    public List<UserSessionLinkerVO> unfinishedMemberLinkerList;
    public List<NonMoimingUserVO> unfinishedNmuLinkerList;

    private TextView textSessionName, textCurCost, textTotalCost, textCurSenderCnt, textTotalSenderCnt;
    private TextView textSessionCreateInfo;
    private TabLayout sessionStatusTab;
    private ProgressBar sessionProgressBar;
    private Button btnSessionStatusChange;

    TabSessionFinishedMembers finishedMembersTab;
    TabSessionUnFinishedMembers unfinishedMembersTab;
    private FragmentManager fragmentManager;

    public boolean isSessionRefreshing = false;
    public List<UUID> stateChangedUserUuid;
    public List<UUID> stateChangedNmuUuid;
    public List<UUID> stateChangedSentUserUuid;
    public List<UUID> stateChangedSentNmuUuid;

    // 사용할 Dialog 들
    private SessionSettingDialog settingDialog;
    private SessionDeleteDialog deleteDialog;


    // Tab Unfinished Member 체크박스 눌렀을 때 notification list 에서 한번 파싱 시켜주는 곳
    // 또한 Notification List 를 눌렀을 때는 Tab Unfinished Member 에서 파싱 한번 시켜주는 곳
    public SessionCreatorNotificationCallBack checkBoxCallback = new SessionCreatorNotificationCallBack() {
        @Override
        public void changingUserState(UUID userUuid, boolean isChecking, boolean isNotification) {

            // TODO: 1 제어하는 userUuid ArrayList 에 추가합니다
            if (isChecking) {
                stateChangedUserUuid.add(userUuid);
            } else {
                stateChangedUserUuid.remove(userUuid);
            }

            // TODO: 2. 양측으로 서로 보내서 checkbox 한번 파싱하게 한다 (또 누르는 행위를 발생시키면 안됨!
            if (isNotification) {
                // 리스트쪽 아답터 체킹
                unfinishedMembersTab.requestAdapterConfirmCheckedUser();
            } else {
                // 알림쪽 아답터 체킹
                if (adapter != null) {
                    adapter.checkConfirmingUser(stateChangedUserUuid);
                }
                // null 이면 알림이 안온 상태이기 때문에 안해주면 됨.
            }

            checkButtonChangeStatus();
        }

        @Override // Notification list 측에서 delete 를 누를경우 발생하는 곳
        public void deleteNotification(UUID sentUserUuid) {

            for (ReceivedNotificationDTO notis : confirmNotificationList) {

                if (notis.getNotification().getSentUserUuid().equals(sentUserUuid)) {

                    confirmNotificationList.remove(notis);
                }
            }

            if (confirmNotificationList.size() == 0) {
                layoutCreator.setVisibility(View.GONE);
            }
        }

        @Override // 정산 요청 보내는 곳
        public void sendFinishRequest(UUID memberUuid, int personalCost) {


            sendFinishFCMRequest(false, memberUuid, personalCost);

        }

    };


    public void sendFinishFCMRequest(boolean isAllSend, UUID memberUuid, int personalCost) {

        // 1. 개별 요청
        if (!isAllSend) {

            NotificationRequestDTO sentConfirm = new NotificationRequestDTO(
                    memberUuid
                    , curUser.getUuid()
                    , "session"
                    , curGroup.getUuid()
                    , curSession.getUuid()
                    , 1
                    , personalCost + "원 송금 요청 하셨습니다!"
            );

            List<NotificationRequestDTO> notiList = new ArrayList<>();
            notiList.add(sentConfirm);

            TransferModel<List<NotificationRequestDTO>> requestModel = new TransferModel<>(notiList);

            NotificationRetrofitService notificationRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(NotificationRetrofitService.class);
            notificationRetrofit.notificationCreateRequest(requestModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TransferModel<List<String>>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull TransferModel<List<String>> responseModel) {

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                            if (e.getMessage() != null) {

                                Log.e("Notification", e.getMessage());
                            }

                        }

                        @Override
                        public void onComplete() {

                            // TODO: 알림을 Creator Uuid 에게 푸시 알림을 전송한다.
                            try {

                                sendFcmNotification("송금 요청", curUser.getUserName() + "님이 " + personalCost + "원 송금 요청을 하셨습니다!"
                                        , fcmTokenMap.get(memberUuid));


                            } catch (JSONException e) {

                                if (e.getMessage() != null) {
                                    Log.e("Session FCM", e.getMessage());
                                }

                            }
                        }
                    });


        } else { // 2. 단체 요청 // List 에 담아서 보내서 생성하고 다시 받음.

            List<NotificationRequestDTO> notiList = new ArrayList<>();

            for (int i = 0; i < unfinishedMemberLinkerList.size(); i++) {

                UserSessionLinkerVO usLinker = unfinishedMemberLinkerList.get(i);

                NotificationRequestDTO sentConfirm = new NotificationRequestDTO(
                        usLinker.getMoimingUser().getUuid()
                        , curUser.getUuid()
                        , "session"
                        , curGroup.getUuid()
                        , curSession.getUuid()
                        , 1
                        , usLinker.getPersonalCost() + "원 송금 요청 하셨습니다!"
                );

                notiList.add(sentConfirm);
            }

            TransferModel<List<NotificationRequestDTO>> requestModel = new TransferModel<>(notiList);

            NotificationRetrofitService notificationRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(NotificationRetrofitService.class);
            notificationRetrofit.notificationCreateRequest(requestModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TransferModel<List<String>>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull TransferModel<List<String>> responseModel) {

                            fcmRequestList = responseModel.getData();


                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                            // 여기서 보내준다?
                            for (int i = 0; i < fcmRequestList.size(); i++) {

                                String userUuid = fcmRequestList.get(i);

                                for (int j = 0; j < unfinishedMemberLinkerList.size(); j++) {

                                    UserSessionLinkerVO usVo = unfinishedMemberLinkerList.get(j);

                                    if (userUuid.equals(usVo.getMoimingUser().getUuid().toString())) {

                                        try {

                                            sendFcmNotification("송금 요청", curUser.getUserName() + "님이 "
                                                            + usVo.getPersonalCost() + "원 송금 요청을 하셨습니다!"
                                                    , fcmTokenMap.get(usVo.getMoimingUser().getUuid()));


                                        } catch (JSONException e) {

                                            if (e.getMessage() != null) {
                                                Log.e("Session FCM", e.getMessage());
                                            }
                                        }
                                    }

                                }

                            }

                            fcmRequestList.clear();

                        }
                    });
        }

    }

    private List<String> fcmRequestList;


    // Unfinished 에서 Nmu 를 눌렀을 때 발생하는 일
    public SessionCreatorNmuCheckboxCallBack nmuCallback = new SessionCreatorNmuCheckboxCallBack() {
        @Override
        public void changingNmuState(UUID nmuUuid, boolean isChecking) {
            if (isChecking) {
                stateChangedNmuUuid.add(nmuUuid);
            } else {
                stateChangedNmuUuid.remove(nmuUuid);
            }

            checkButtonChangeStatus();
        }
    };

    // Finished 에서 눌렀을 때 발생하는 일
    public SessionCreatorSentCheckboxCallBack sentUserCallback = new SessionCreatorSentCheckboxCallBack() {
        @Override
        public void changeSentUserState(UUID userUuid, boolean isChecking, boolean isMoimingUser) {

            if (isChecking) {
                if (isMoimingUser) {
                    stateChangedSentUserUuid.add(userUuid);
                } else {
                    stateChangedSentNmuUuid.add(userUuid);
                }
            } else {
                if (isMoimingUser) {
                    stateChangedSentUserUuid.remove(userUuid);
                } else {
                    stateChangedSentNmuUuid.remove(userUuid);
                }
            }

            checkButtonChangeStatus();

        }
    };


    private void checkButtonChangeStatus() {

        // 4개가 다 Empty 면 못함
        if (stateChangedUserUuid.isEmpty() && stateChangedNmuUuid.isEmpty() && stateChangedSentNmuUuid.isEmpty() && stateChangedSentUserUuid.isEmpty()) {

            btnSessionStatusChange.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_light_main_bg, null));
            btnSessionStatusChange.setEnabled(false);
        } else {

            btnSessionStatusChange.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_main_bg, null));
            btnSessionStatusChange.setEnabled(true);
        }
    }


    private TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (tab.getPosition()) {
                case 0:

                    transaction.hide(finishedMembersTab);
                    transaction.show(unfinishedMembersTab);

                    break;

                case 1:

                    transaction.hide(unfinishedMembersTab);
                    transaction.show(finishedMembersTab);

                    break;
            }
            transaction.commit();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void createConfirmNotification() {

        NotificationRequestDTO sentConfirm = new NotificationRequestDTO(
                creatorInfo.getUuid()
                , curUser.getUuid()
                , "session"
                , curGroup.getUuid()
                , curSession.getUuid()
                , 2
                , "송금 확인을 요청하였습니다!"
        );

        List<NotificationRequestDTO> notiList = new ArrayList<>();
        notiList.add(sentConfirm);

        TransferModel<List<NotificationRequestDTO>> requestModel = new TransferModel<>(notiList);

        NotificationRetrofitService notificationRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(NotificationRetrofitService.class);
        notificationRetrofit.notificationCreateRequest(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<String>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<String>> responseModel) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            Log.e("Notification", e.getMessage());
                        }

                    }

                    @Override
                    public void onComplete() {

                        // TODO: 알림을 Creator Uuid 에게 푸시 알림을 전송한다.
                        try {

                            sendFcmNotification("송금 확인 요청", curUser.getUserName() + "님이 송금 확인 요청하셨습니다!", sessionCreatorFcmToken);

                            // 버튼 변경
                            btnSendConfirm.setClickable(false);
                            btnSendConfirm.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_light_main_bg, null));
                            textSendBtn.setText("확인 중");


                        } catch (JSONException e) {

                            if (e.getMessage() != null) {
                                Log.e("Session FCM", e.getMessage());
                            }

                        }
                    }
                });

    }


    private void sendFcmNotification(String title, String text, String receiverToken) throws JSONException {

        JSONObject jsonSend = FCMRequest.getInstance().buildJsonBody(title, text, receiverToken);

        RequestBody reBody = RequestBody.create(MediaType.parse("application/json, charset-utf8")
                , String.valueOf(jsonSend));

        FCMRequest.getInstance().sendFcmRequest(getApplicationContext(), reBody);

    }

    // 송금 완료된 인원들의 상태를 바꾼다, 혹은
    private void changeSessionStatus() {

        // TODO: 이거 보내면 됨.
        SessionStatusChangeDTO sessionStatusChange = new SessionStatusChangeDTO(curSession.getUuid(), stateChangedUserUuid, stateChangedNmuUuid
                , stateChangedSentUserUuid, stateChangedSentNmuUuid);

        SessionRetrofitService sessionRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(SessionRetrofitService.class);
        sessionRetrofit.updateSessionStatus(new TransferModel<>(sessionStatusChange))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<MoimingSessionResponseDTO>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<MoimingSessionResponseDTO> responseModel) {

                        curSession = responseModel.getData().convertToVO();


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            Log.e("Session Refresh Error:: ", e.getMessage());
                        }

                    }

                    @Override
                    public void onComplete() {

                        isSessionRefreshing = true;
                        GroupActivity.SESSION_LIST_REFRESH_FLAG = true;

                        confirmNotificationList.clear();

                        unfinishedMemberLinkerList.clear();
                        unfinishedNmuLinkerList.clear();
                        finishedMemberLinkerList.clear();
                        finishedNmuLinkerList.clear();

                        // 또한 보내는 uuid list 도 클리어 해야한다

                        stateChangedNmuUuid.clear();
                        stateChangedSentNmuUuid.clear();
                        stateChangedSentUserUuid.clear();
                        stateChangedUserUuid.clear();

                        checkButtonChangeStatus();

                        initSessionMembers();

//                        setSessionUi();

                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        receiveIntent();

        initView();

        initParams();

        initSessionMembers();


    }

    private List<ReceivedNotificationDTO> confirmNotificationList;

    private void checkSendCheckNotification() {

        NotificationRetrofitService notiRetro = GlobalRetrofit.getInstance().getRetrofit().create(NotificationRetrofitService.class);
        NotificationUserAndActivityDTO uuidInfo = new NotificationUserAndActivityDTO(curUser.getUuid(), null, curSession.getUuid());

//        notiRetro.getUserNotification("session", String.valueOf(2), curUser.getUuid().toString(), curSession.getUuid().toString())
        notiRetro.getUserNotification("session", String.valueOf(2), uuidInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<ReceivedNotificationDTO>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<ReceivedNotificationDTO>> responseModel) {

                        confirmNotificationList = responseModel.getData();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            Log.e("Notification Error", e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                        if (confirmNotificationList.size() != 0) {

                            if (!isSessionRefreshing) {
                                // 있으면 체크박스와 함께 보여준다.
                                initNotificationRecycler();
                            } else {

                                adapter.notifyDataSetChanged();
                            }
                        } else {

                            layoutCreator.setVisibility(View.GONE);

                        }

                    }
                });

    }

    private void receiveIntent() {

        Intent dataIntent = getIntent();

        curUserStatus = dataIntent.getExtras().getInt("cur_user_status");
        curGroup = (MoimingGroupVO) dataIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));
        curUser = (MoimingUserVO) dataIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        curSession = (MoimingSessionVO) dataIntent.getExtras().getSerializable(GroupActivity.MOIMING_SESSION_DATA_KEY);

    }

    private void initView() {

        layoutTotal = findViewById(R.id.layout_session_total);

        sessionProgressBar = findViewById(R.id.progress_session);

        textSessionName = findViewById(R.id.text_session_name); // 상단 앱바에 세션 이름
        textSessionName.setText(curSession.getSessionName());

        textCurCost = findViewById(R.id.text_cur_cost); // 프로그래스 바 위에 현재 모금 금액
        textTotalCost = findViewById(R.id.text_total_cost); // 동일 위치, 해당 세션 총 모금 금액


        textCurSenderCnt = findViewById(R.id.text_cur_sender);
        textTotalSenderCnt = findViewById(R.id.text_total_sender);

        textSessionCreateInfo = findViewById(R.id.text_session_create_info);

        sessionStatusTab = findViewById(R.id.tab_session_activity);
        sessionStatusTab.addOnTabSelectedListener(tabListener);

        layoutSessionStatus = findViewById(R.id.layout_session_status);

        // 현재 유저가 어떤 유저인지에 따라 바뀌는 ..
        imgCreator = findViewById(R.id.img_creator);
        layoutCreator = findViewById(R.id.layout_creator_status);
        layoutMember = findViewById(R.id.layout_member_status);
        textMemberFinished = findViewById(R.id.text_member_finished);

        btnEditSession = findViewById(R.id.btn_edit_session);

        // TODO: 총무가 그룹을 나갔을 경우도 가능한데, 이건 나중에 확인.
        if (curUser.getUuid().toString().equals(curSession.getSessionCreatorUuid().toString())) { // 내가 총무일 경우
            btnEditSession.setVisibility(View.VISIBLE);
        } else {
            btnEditSession.setVisibility(View.GONE);
        }

        btnEditSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 다이얼로그 발생, 현재 삭제 밖에 없음.
                settingDialog.show();

            }
        });

        // 정산 필요한 멤버인 경우
        btnSendConfirm = findViewById(R.id.btn_send_confirm);
        textSendBtn = findViewById(R.id.text_send_btn);
        btnSendConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 확인요청을 보내시겠습니까 Dialog 발생
                createConfirmNotification();

            }
        });


        btnSessionStatusChange = findViewById(R.id.btn_change_session_status);
        btnSessionStatusChange.setEnabled(false);
        btnSessionStatusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeSessionStatus();
            }
        });

        confirmNotiRecycler = findViewById(R.id.confirm_notification_recycler);

        parseStatus();

    }


    private void setSessionUi() {

        // 금액 세팅
        textCurCost.setText(String.valueOf(curSession.getCurCost()));
        String totalCost = curSession.getTotalCost() + " 원";
        textTotalCost.setText(totalCost);

        // 인원 세팅
        textCurSenderCnt.setText(String.valueOf(curSession.getCurSenderCnt()));
        String totalSenderCnt = curSession.getSessionMemberCnt() + " 명";
        textTotalSenderCnt.setText(totalSenderCnt);

        setCreateInfoText();

        setProgressBar();

    }

    private void setCreateInfoText() {

        // TODO: 여기에 통장 Info 도 표시해주면 되겠다.

        LocalDateTime createdDateTime = curSession.getCreatedAt();

        String createInfoText = transferDateToText(createdDateTime);

        String creatorName = creatorInfo.getUserName();

        createInfoText = createInfoText + "에 " + creatorName + "님이 생성";

        if (creatorInfo.getUuid().equals(curUser.getUuid())) {
            createInfoText = createInfoText + "에 내가 생성";
        }
        textSessionCreateInfo.setText(createInfoText);

    }


    private String transferDateToText(LocalDateTime createdDate) {


        String year = String.valueOf(createdDate.getYear());

        String month = "0";

        int intMonth = createdDate.getMonthValue();

        if (intMonth < 10) {
            month += String.valueOf(intMonth);
        } else {
            month = String.valueOf(intMonth);
        }

        String day = "0";

        int intDayOfMonth = createdDate.getDayOfMonth();

        if (intDayOfMonth < 10) {
            day += String.valueOf(intDayOfMonth);
        } else {
            day = String.valueOf(intDayOfMonth);
        }


        String dateText = year + "." + month + "." + day;

        return dateText;

    }

    private void setProgressBar() {

        int curSender = curSession.getCurSenderCnt();
        int totalSender = curSession.getSessionMemberCnt();

        int progress = (int) (((double) curSender / (double) totalSender) * 100);

        sessionProgressBar.setProgress(progress);

    }

    private void parseStatus() {

        ConstraintSet sessionStatusSet = new ConstraintSet();

        /**
         0 = 내 정산 1 = 송금 필요 2 = 송금 완료 3 = 송금 확인 중 4 = 미참여
         */
        if (curUserStatus != 0) { // 이 정산은 내가 보내야 하는 정산!

            layoutCreator.setVisibility(View.GONE);
            imgCreator.setVisibility(View.GONE);

            if (curUserStatus == 1 || curUserStatus == 3) { // 송금 필요함 (송금 확인중일 떄도 따로)

                layoutMember.setVisibility(View.VISIBLE);
                textMemberFinished.setVisibility(View.GONE);

                if (curUserStatus == 3) { // 송금 확인 요청 중
                    // 버튼 변경
                    btnSendConfirm.setClickable(false);
                    btnSendConfirm.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_light_main_bg, null));
                    textSendBtn.setText("확인 중");
                }

                sessionStatusSet.clone(layoutTotal);
                sessionStatusSet.connect(layoutSessionStatus.getId(), ConstraintSet.TOP, layoutMember.getId(), ConstraintSet.BOTTOM);

            } else if (curUserStatus == 2) { // 송금 완료 함

                layoutMember.setVisibility(View.GONE);
                textMemberFinished.setVisibility(View.VISIBLE);

                sessionStatusSet.clone(layoutTotal);
                sessionStatusSet.connect(layoutSessionStatus.getId(), ConstraintSet.TOP, textMemberFinished.getId(), ConstraintSet.BOTTOM);

            } else if (curUserStatus == 4) { // 미참여

                layoutMember.setVisibility(View.GONE);
                textMemberFinished.setText("참여하는 활동이 아닙니다");
                textMemberFinished.setVisibility(View.VISIBLE);

                sessionStatusSet.clone(layoutTotal);
                sessionStatusSet.connect(layoutSessionStatus.getId(), ConstraintSet.TOP, textMemberFinished.getId(), ConstraintSet.BOTTOM);

            }

            sessionStatusSet.applyTo(layoutTotal);


        } else { // 이 정산은 내가 만든 정산! 0일 경우

            // 고정
            imgCreator.setVisibility(View.VISIBLE);
            layoutMember.setVisibility(View.GONE);
            textMemberFinished.setVisibility(View.GONE);

            // 알림이 있을 경우
            layoutCreator.setVisibility(View.VISIBLE);

            sessionStatusSet.clone(layoutTotal);
            sessionStatusSet.connect(layoutSessionStatus.getId(), ConstraintSet.TOP, layoutCreator.getId(), ConstraintSet.BOTTOM);

            sessionStatusSet.applyTo(layoutTotal);

        }
    }

    private SessionDialogCallBack settingCallback = new SessionDialogCallBack() {
        // TODO: 정산활동 편집 추후 추가 예정
        @Override
        public void selectDelete() {

            deleteDialog.show();

        }

        @Override
        public void deleteSession() {

            processDeleteSession();
        }

    };


    private SessionConfirmNotificationAdapter adapter;

    private void initNotificationRecycler() {

        LinearLayoutManager membersLayoutManager = new LinearLayoutManager(this);
        confirmNotiRecycler.setLayoutManager(membersLayoutManager);

        adapter = new SessionConfirmNotificationAdapter(getApplicationContext(), confirmNotificationList, checkBoxCallback, curSession.getUuid());
        confirmNotiRecycler.setAdapter(adapter);


    }

    private void initParams() {

        stateChangedUserUuid = new ArrayList<>();
        stateChangedNmuUuid = new ArrayList<>();
        stateChangedSentUserUuid = new ArrayList<>();
        stateChangedSentNmuUuid = new ArrayList<>();

        unfinishedMemberLinkerList = new ArrayList<>();
        unfinishedNmuLinkerList = new ArrayList<>();

        finishedMemberLinkerList = new ArrayList<>();
        finishedNmuLinkerList = new ArrayList<>();

        // 현재 리스트는 직접 접근해서 가져감.
        finishedMembersTab = new TabSessionFinishedMembers();
        unfinishedMembersTab = new TabSessionUnFinishedMembers();

        fragmentManager = getSupportFragmentManager();

        // 사용할 다이얼로그들
        settingDialog = new SessionSettingDialog(this, settingCallback);
        deleteDialog = new SessionDeleteDialog(this, settingCallback, curSession.getSessionName());

    }

    private void initSessionMembers() {

        USLinkerRetrofitService linkerRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(USLinkerRetrofitService.class);

        linkerRetrofit.requestSessionLinker(curSession.getUuid().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<SessionMembersDTO>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<SessionMembersDTO> receivedDTO) {

                        SessionMembersDTO sessionMembers = receivedDTO.getData();

                        creatorInfo = sessionMembers.getSessionCreatorInfo();

                        List<USLinkerResponseDTO> memberLinkerDTO = sessionMembers.getSessionMoimingMemberList();
                        List<NonMoimingUserResponseDTO> nmuLinkerDTO = sessionMembers.getSessionNmuList();

                        // TODO: 해야할일 -> 송금 유저들 골라서 Fragment 에 뿌려줘야함. 아니면 S.U 해놓고 Fragment 에서 가져갈 수 있도록 제어해주기.

                        for (USLinkerResponseDTO dto : memberLinkerDTO) {

                            UserSessionLinkerVO usVo = dto.convertToVO();

                            // 총무인 경우 해당 세션 멤버들의 uuid를 모두 맵에 저장해놓는다.
                            if (curUserStatus == 0 && !isSessionRefreshing) {

                                fcmTokenMap.put(usVo.getMoimingUser().getUuid(), "");

                            }

                            if (usVo.isSent()) {
//                                sessionMemberLinkerList.add(dto.convertToVO());
                                finishedMemberLinkerList.add(usVo);

                            } else {

                                unfinishedMemberLinkerList.add(usVo);
                            }
                        }


                        for (NonMoimingUserResponseDTO dto : nmuLinkerDTO) {

                            NonMoimingUserVO nmuVo = dto.convertToVO();

                            if (nmuVo.isNmuSent()) {

                                finishedNmuLinkerList.add(nmuVo);

                            } else {

                                unfinishedNmuLinkerList.add(nmuVo);

                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            Log.e("Session", e.getMessage());

                        }
                    }

                    @Override
                    public void onComplete() {

                        if (!isSessionRefreshing) {
                            prepareFcmTokens();
                        } else { // RecyclerView Tab 내 Refresh

                            unfinishedMembersTab.initRecyclerView();
                            finishedMembersTab.initRecyclerView();
                        }

                        setSessionUi();

                        //TODO: 여기서 Progress Bar?
                        if (curUserStatus == 0) { // 총무는 받은 알림요청이 있는지 확인한다.

                            checkSendCheckNotification();
                        }

                        if (isSessionRefreshing) {
                            isSessionRefreshing = false;
                        }

                    }
                });
    }

    private void processDeleteSession() {

        SessionRetrofitService sessionRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(SessionRetrofitService.class);

        sessionRetrofit.deleteSession(new TransferModel<>(curSession.getUuid().toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<String> responseModel) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            Log.e("Session Delete:: ", e.getMessage());
                        }

                    }

                    @Override
                    public void onComplete() {

                        Toast.makeText(getApplicationContext(), "정산활동이 삭제되었습니다", Toast.LENGTH_SHORT).show();

                        GroupActivity.SESSION_LIST_REFRESH_FLAG = true;

                        finish();
                    }
                });

    }


    public String getCurUserUuid() {

        return curUser.getUuid().toString();
    }

    public MoimingSessionVO getCurSession() {

        return curSession;

    }

    private void prepareFcmTokens() {

        // FCM Token 들 준비해놓기.
        if (curUserStatus != 0) { // 내가 보내야 하는 정산, 생성자의 uuid 필요

            DocumentReference keyDocs = firebaseDB.collection("UserInfo").document(creatorInfo.getUuid().toString()); // uuid 가 문서 명임.

            keyDocs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull @NotNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        DocumentSnapshot snapshot = task.getResult();

                        if (snapshot.exists()) {

                            Map<String, Object> responseSnap = snapshot.getData();
                            sessionCreatorFcmToken = (String) responseSnap.get("fcm_token");

                        } else {

                            Log.e("Firebase Error", "해당 유저의 토큰 정보가 없습니다");
                        }

                        // 여기서 마무리 하는 것
                        FragmentTransaction transaction = fragmentManager.beginTransaction();

                        transaction.add(R.id.frame_session_status, unfinishedMembersTab)
                                .add(R.id.frame_session_status, finishedMembersTab).hide(finishedMembersTab).commit();


                    } else {
                        Log.e("Firebase Error", "해당 문서를 불러오지 못했습니다");
                    }
                }
            });

        } else { // 내가 총무인 정산

            // 세션 멤버들 모두의 FCM Token 이 필요하다.
            Set<UUID> keySet = fcmTokenMap.keySet();
            Iterator<UUID> iter = keySet.iterator();

            tokenMapSize = keySet.size();

            while (iter.hasNext()) { // 근데 이건 다 uuid 생기면 해야겠다.

                String memberUuid = iter.next().toString();
                DocumentReference keyDocs = firebaseDB.collection("UserInfo").document(memberUuid); // uuid 가 문서 명임.

                keyDocs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull @NotNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            DocumentSnapshot snapshot = task.getResult();
                            tokenMapSize--;

                            if (snapshot.exists()) {

                                Map<String, Object> responseSnap = snapshot.getData();
                                fcmTokenMap.put(UUID.fromString(memberUuid), (String) responseSnap.get("fcm_token"));

                            } else {

                                Log.e("Firebase Error", "해당 유저의 토큰 정보가 없습니다");
                            }

                            if (tokenMapSize == 0) {

                                // 여기서 마무리 하는 것
                                FragmentTransaction transaction = fragmentManager.beginTransaction();

                                transaction.add(R.id.frame_session_status, unfinishedMembersTab)
                                        .add(R.id.frame_session_status, finishedMembersTab).hide(finishedMembersTab).commit();

                                tokenMapSize = keySet.size();

                            }

                        } else {
                            Log.e("Firebase Error", "해당 문서를 불러오지 못했습니다");
                        }
                    }
                });

            }


        }
    }

    private int tokenMapSize;

}