package com.example.moimingrelease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_listener_interface.FCMCallBack;
import com.example.moimingrelease.fragments_main.MoimingHomeFragment;
import com.example.moimingrelease.fragments_main.MoimingReserveFragment;
import com.example.moimingrelease.moiming_model.dialog.AppProcessDialog;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.NotificationVO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingUserResponseDTO;
import com.example.moimingrelease.moiming_model.response_dto.NotificationResponseDTO;
import com.example.moimingrelease.network.NotificationRetrofitService;
import com.example.moimingrelease.network.fcm.FCMAppToken;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.UserRetrofitService;
import com.example.moimingrelease.network.kakao_api.KakaoFriends;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Friend;
import com.kakao.sdk.talk.model.Friends;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    /**
     * MoimingHomeFragment 에서는 유저가 포함되어야 하는 그룹들을 생성해 놓는다
     * TODO: GroupActivity 에서 GroupMembersDTO 없어도 됨. 정산활동 생성에서도 GroupMembersDTO 없어도 됨.
     */

    public final static String MAIN_TAG = "MAIN_LOG";
    public final static String MOIMING_USER_DATA_KEY = "current_moiming_user";

    public final static String FIXED_GROUP_SP_NAME = "FIXED_GROUP_SP";
    public final static String FIXED_GROUP_UUID_KEY = "FIXED_GROUP";

    public static boolean IS_USER_UPDATED = false;
    public static boolean IS_NOTIFICATION_REFRESH_NEEDED = false; // Notification 만 Refresh 되면 된다.
    public static boolean IS_MAIN_GROUP_INFO_REFRESH_NEEDED = false; // 메인화면 전체가 Refresh 되어야 함 Notification 포함

    public AppProcessDialog processDialog;

    public MoimingUserVO curMoimingUser;

    private ExtendedFloatingActionButton btnDutchpay, btnDutchpayOld, btnDutchpayNew;
    private FrameLayout mainFrame;
    private View transparent_bg;
    //    private Animation main_fab_open, main_fab_close;

    // FAB Animations
//    private TranslateAnimation mainFabOpen, mainFabClose;
    private boolean isFabOpen = false;
//    private final int openYDelta = 100;
//    private final int closeYDelta = -100;

    private BottomNavigationView mainNavigation;
    private ImageView btnSetting, btnNotification, btnAdd, btnSearch;
    private TextView viewNotification;

    private FragmentManager fragmentManager;
    MoimingHomeFragment homeFragment;
    MoimingReserveFragment reserveFragment;

    public List<ReceivedNotificationDTO> rawNotificationList;
    public Map<UUID, List<ReceivedNotificationDTO>> rawNotificationMap;

    // 만약 종료시 눌러서 해당 Activity 로 이동해야할 때
    private String movingGroupUuid;
    private String movingSessionUuid;


    private BottomNavigationView.OnNavigationItemSelectedListener mainNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (id) {

                case R.id.btn_main_home:

                    fragmentTransaction.replace(R.id.frame_main, homeFragment).commit();

                    btnAdd.setVisibility(View.VISIBLE);
                    btnSearch.setVisibility(View.VISIBLE);

                    break;

                case R.id.btn_main_reserve:


/*
                    fragmentTransaction.replace(R.id.frame_main, reserveFragment).commit();

                    btnAdd.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.GONE);*/

                    break;


            }
            return true;
        }
    };

    private View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int fabId = view.getId();

            switch (fabId) {

                case R.id.btn_dutchpay:
                    fabAction();
                    break;

                case R.id.btn_dutchpay_old:
                    Toast.makeText(getApplicationContext(), "기존 그룹원들과 더치페이를 합니다.", Toast.LENGTH_SHORT).show();

                    Intent toSessionInOldGroup = new Intent(MainActivity.this, SessionCreationInOldGroupActivity.class);

                    toSessionInOldGroup.putExtra(getResources().getString(R.string.moiming_user_data_key), curMoimingUser);
                    toSessionInOldGroup.putParcelableArrayListExtra(getResources().getString(R.string.moiming_group_and_members_data_key), homeFragment.getGroupAndMemberDataList());

                    startActivity(toSessionInOldGroup);

                    fabAction();

                    break;

                case R.id.btn_dutchpay_new:

                    Toast.makeText(getApplicationContext(), "새 그룹원들과 더치페이를 합니다.", Toast.LENGTH_SHORT).show();

                    Intent toSessionInNewGroup = new Intent(MainActivity.this, SessionCreationActivity.class);

                    toSessionInNewGroup.putExtra(getResources().getString(R.string.moiming_user_data_key), curMoimingUser);
                    toSessionInNewGroup.putExtra(getResources().getString(R.string.session_creation_with_new_group_flag), true);

                    startActivity(toSessionInNewGroup);

                    fabAction();

                    break;

            }

        }
    };


    private void prepareKakaoFriends() {

        TalkApiClient.getInstance().friends(new Function2<Friends<Friend>, Throwable, Unit>() {
            @Override
            public Unit invoke(Friends<Friend> friendFriends, Throwable throwable) {

                if (throwable == null) {

                    KakaoFriends.getINSTANCE().setKakaoFriendsList(friendFriends.component2());
                    Log.w(MAIN_TAG, "카카오 친구들을 동기화하였습니다\n" + KakaoFriends.getINSTANCE().getKakaoFriendsList().toString());

                } else { // 오류 발생

                    Log.e(MAIN_TAG, throwable.getMessage());
                    Toast.makeText(getApplicationContext(), "카카오 친구들을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    finish();
                }

                return null;
            }
        });

    }

    private Button btnAccessTokenView;

    private FCMCallBack fcmCallBack = new FCMCallBack() {
        @Override
        public void onSuccess() {

            Log.w("ACC_TOKEN", "Successful!");

        }

        @Override
        public void onFailure() {

            Log.w("ACC_TOKEN", "Failed!");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("FLAG_TAG: 3", String.valueOf(MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED));
        Log.e("Working Log 2: ", "WORKS!");

        receiveIntent();
        Log.e("Working Log 3: ", "WORKS!");

        prepareKakaoFriends();
        Log.e("Working Log 4: ", "WORKS!");

        initView();
        Log.e("Working Log 5: ", "WORKS!");

        initParams();
        Log.e("Working Log 6: ", "WORKS!");


        try {

            FCMAppToken.getFcmAppAccessToken(this, fcmCallBack);

        } catch (IOException e) {

            Log.w("ACC_TOKEN", e.getMessage());

        }

        receiveNotification();
        Log.e("Working Log 7: ", "WORKS!");


        //fab listeners
        btnDutchpay.setOnClickListener(fabListener);
        btnDutchpayNew.setOnClickListener(fabListener);
        btnDutchpayOld.setOnClickListener(fabListener);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toGroupCreation = new Intent(MainActivity.this, GroupCreationActivity.class);

                toGroupCreation.putExtra(getResources().getString(R.string.moiming_user_data_key), curMoimingUser);

                startActivity(toGroupCreation);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toGroupSearch = new Intent(MainActivity.this, SearchGroupActivity.class);

                toGroupSearch.putExtra(getResources().getString(R.string.moiming_user_data_key), curMoimingUser);
                toGroupSearch.putParcelableArrayListExtra(getResources().getString(R.string.search_group_data_key), homeFragment.getGroupAndMemberDataList());

                startActivity(toGroupSearch);

                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            }
        });


        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toNotification = new Intent(MainActivity.this, NotificationActivity.class);

                toNotification.putExtra(getResources().getString(R.string.moiming_user_data_key), curMoimingUser);
                toNotification.putParcelableArrayListExtra(getResources().getString(R.string.moiming_group_and_members_data_key), homeFragment.getGroupAndMemberDataList());
                toNotification.putParcelableArrayListExtra(getResources().getString(R.string.moiming_notification_data_key), (ArrayList<? extends Parcelable>) rawNotificationList);

                startActivity(toNotification);

            }
        });


        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toSetting = new Intent(MainActivity.this, MyPageActivity.class);

                toSetting.putExtra(getResources().getString(R.string.moiming_user_data_key), curMoimingUser);

                startActivity(toSetting);

            }
        });


    }


    private void receiveIntent() {

        Intent dataReceived = getIntent();

        if (dataReceived.getExtras() != null) {
            curMoimingUser = (MoimingUserVO) dataReceived.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            boolean isFcmClicked = dataReceived.getBooleanExtra("is_fcm_clicked", false);
//            Log.e("Working Log:: ", "result: " + IS_MAIN_GROUP_INFO_REFRESH_NEEDED);

            movingGroupUuid = dataReceived.getExtras().getString(getResources().getString(R.string.main_move_to_group_key), "");
            movingSessionUuid = dataReceived.getExtras().getString(getResources().getString(R.string.group_move_to_session_key), "");


            if (isFcmClicked) {

                IS_MAIN_GROUP_INFO_REFRESH_NEEDED = false;
            }
        }
    }

    public String getMovingGroupUuid() {

        return this.movingGroupUuid;

    }

    public String getMovingSessionUuid() {

        return this.movingSessionUuid;
    }

    private void receiveNotification() {

        processDialog.show();

        // 자꾸 한번씩 튕긴다..
        String userUuid = curMoimingUser.getUuid().toString();

        NotificationRetrofitService notiRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(NotificationRetrofitService.class);

        notiRetrofit.getUserAllNotification(new TransferModel<>(userUuid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<ReceivedNotificationDTO>>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull TransferModel<List<ReceivedNotificationDTO>> responseModel) {

                        // TODO: List<NOTIFICATION> 은 알림 파트에 보내기 위해서임!
                        //       해당 List 에서 unread 갯수를 표시해준다
                        rawNotificationList.clear();
                        rawNotificationMap.clear();

                        rawNotificationList = responseModel.getData();

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                        if (e.getMessage() != null) {
                            Log.e("Notification Receive Error:: ", e.getMessage());
                        }

                    }

                    @Override
                    public void onComplete() {

                        Log.e("Working Log 9: ", "WORKS!");
                        parseNotification();

                        // 나머지 아답터에 전달해서 아답터에서 해결!
                        if (IS_NOTIFICATION_REFRESH_NEEDED || IS_MAIN_GROUP_INFO_REFRESH_NEEDED) { //  이거 중이라서 여기 온거면
                            Log.e("Working Log 10: ", "WORKS!" + IS_NOTIFICATION_REFRESH_NEEDED + "," + IS_MAIN_GROUP_INFO_REFRESH_NEEDED);

                            if (!IS_MAIN_GROUP_INFO_REFRESH_NEEDED) { // 얘도 해야하면 차피 그 쪽에서 Refresh 할 거임
                                Log.e("Working Log 11: ", "WORKS!" + IS_NOTIFICATION_REFRESH_NEEDED + "," + IS_MAIN_GROUP_INFO_REFRESH_NEEDED);

                                // TODO: 그 쪽에서 Notification 만 다시 박는다.
                                homeFragment.applyRefreshedNotification();

                            } else {
                                Log.e("Working Log 12: ", "WORKS!" + IS_NOTIFICATION_REFRESH_NEEDED + "," + IS_MAIN_GROUP_INFO_REFRESH_NEEDED);

                                homeFragment.getUgLinkers();

                                IS_MAIN_GROUP_INFO_REFRESH_NEEDED = false;
                            }

                        } else { // 처음 실행

                            Log.e("Working Log 13: ", "WORKS!" + IS_NOTIFICATION_REFRESH_NEEDED + "," + IS_MAIN_GROUP_INFO_REFRESH_NEEDED);

                            mainNavigation.getMenu().getItem(1).setEnabled(false);
                            mainNavigation.setOnNavigationItemSelectedListener(mainNavListener);
                            mainNavigation.setSelectedItemId(R.id.btn_main_home);
                        }

                    }
                });

    }

    private void parseNotification() {

        // TODO: 1. Unread 갯수만 파악후 알림 표시 해놓고, 나머지는 아답터로 전달한다. .
        int totalUnreadCnt = 0;

        for (int i = 0; i < rawNotificationList.size(); i++) {

            ReceivedNotificationDTO notificationDto = rawNotificationList.get(i);
            NotificationVO notification = notificationDto.getNotification().convertToVO();

            if (!notification.getRead()) {
                totalUnreadCnt++;
            }
        }

        if (totalUnreadCnt == 0) {
            viewNotification.setVisibility(View.GONE);
        } else {
            viewNotification.setVisibility(View.VISIBLE);
            if (totalUnreadCnt >= 99) {
                viewNotification.setText("99");
            } else {
                viewNotification.setText(String.valueOf(totalUnreadCnt));
            }
        }

        // TODO: CF: is Read 의 기준은 notification 화면에서 눌러서 들어가거나,
        //          해당 그룹, 해당 세션으로 들어가는 행위는 읽었다라고 간주. --> 즉, 들어갈 때마다 is Read 에 대한 행위를 어떻게 할지 표시 필요.
        //          is Read Refresh에 대해서 전체적인 구성 후에 개발 필요해 보임.

    }

    private void initParams() {

        // 다이얼로그 퍼블릭화
        processDialog = new AppProcessDialog(MainActivity.this);

        // 변수 확인
        fragmentManager = getSupportFragmentManager();

        // FG 정의
        homeFragment = new MoimingHomeFragment();
        reserveFragment = new MoimingReserveFragment();

        rawNotificationList = new ArrayList<>();
        rawNotificationMap = new HashMap<>();

    }


    private void initView() {

        mainFrame = findViewById(R.id.frame_main);
        viewNotification = findViewById(R.id.view_notification_cnt);

        mainNavigation = findViewById(R.id.main_navigation);

        // FAB Settings
        btnDutchpay = findViewById(R.id.btn_dutchpay);
        btnDutchpayNew = findViewById(R.id.btn_dutchpay_new);
        btnDutchpayNew.setClickable(false);
        btnDutchpayOld = findViewById(R.id.btn_dutchpay_old);
        btnDutchpayOld.setClickable(false);

        transparent_bg = findViewById(R.id.transparent_bg);
        transparent_bg.bringToFront(); // Fade 효과를 주기 위함.

        // Upper Btns
        btnSetting = findViewById(R.id.btn_setting);
        btnNotification = findViewById(R.id.btn_notification);
        btnAdd = findViewById(R.id.btn_add);
        btnSearch = findViewById(R.id.btn_search);

    }

    private void fabAction() {

        if (isFabOpen) {

            transparent_bg.setVisibility(View.GONE);

            btnDutchpayOld.setClickable(false);
            btnDutchpayOld.setVisibility(View.GONE);
            btnDutchpayNew.setClickable(false);
            btnDutchpayNew.setVisibility(View.GONE);

            // 나머지 만질 수 있게 함
            btnAdd.setEnabled(true);
            btnNotification.setEnabled(true);
            btnSearch.setEnabled(true);
            btnSetting.setEnabled(true);
            homeFragment.setAdapterRecyclerClickable(true);


            isFabOpen = false;

        } else {

            transparent_bg.setVisibility(View.VISIBLE);

            btnDutchpayOld.setClickable(true);
            btnDutchpayOld.setVisibility(View.VISIBLE);
            btnDutchpayNew.setClickable(true);
            btnDutchpayNew.setVisibility(View.VISIBLE);

            // 나머지 못 만지게함
            btnAdd.setEnabled(false);
            btnNotification.setEnabled(false);
            btnSearch.setEnabled(false);
            btnSetting.setEnabled(false);
            homeFragment.setAdapterRecyclerClickable(false);


            isFabOpen = true;
        }
    }

    // 다른 액티비티 후에 돌아올 경우 onRestart 가 실행된다.
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("FLAG_TAG: 4", String.valueOf(MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED));

        if (IS_USER_UPDATED) { // Refresh 가 필요한 경우

            // User Info Update
            updateCurUser();

            IS_USER_UPDATED = false;

        }

        if (IS_NOTIFICATION_REFRESH_NEEDED || IS_MAIN_GROUP_INFO_REFRESH_NEEDED) { // 뭐든 일단 receiveNotification Refresh 는 필요하다.

            receiveNotification();

        }


    }

    private void updateCurUser() {

        UserRetrofitService userRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(UserRetrofitService.class);

        userRetrofit.getCurUser(curMoimingUser.getUuid().toString())
                .enqueue(new Callback<TransferModel<MoimingUserResponseDTO>>() {
                    @Override
                    public void onResponse(Call<TransferModel<MoimingUserResponseDTO>> call, Response<TransferModel<MoimingUserResponseDTO>> response) {

                        TransferModel<MoimingUserResponseDTO> responseModel = response.body();

                        curMoimingUser = responseModel.getData().convertToVO();

                        Toast.makeText(getApplicationContext(), "유저 REFRESHED", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<TransferModel<MoimingUserResponseDTO>> call, Throwable t) {

                        Toast.makeText(getApplicationContext(), "유저 정보 통신에 문제가 생겼습니다. 잠시 후 다시 접속해 주세요", Toast.LENGTH_SHORT).show();
                        if (t.getMessage() != null) Log.e(MAIN_TAG, t.getMessage());
                        finish();

                    }
                });
    }
}