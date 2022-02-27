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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_listener_interface.FCMCallBack;
import com.example.moimingrelease.fragments_main.MoimingHomeFragment;
import com.example.moimingrelease.fragments_main.MoimingReserveFragment;
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
    public static boolean IS_NOTIFICATION_REFRESH_NEEDED = false;
    public static boolean IS_MAIN_GROUP_INFO_REFRESH_NEEDED = false;



    public MoimingUserVO curMoimingUser;

    private ExtendedFloatingActionButton btnDutchpay, btnDutchpayOld, btnDutchpayNew;
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

                    break;


                // TODO: 이거 해야 함!
                case R.id.btn_dutchpay_new:

                    Toast.makeText(getApplicationContext(), "새 그룹원들과 더치페이를 합니다.", Toast.LENGTH_SHORT).show();

                    Intent toSessionInNewGroup = new Intent(MainActivity.this, SessionCreationActivity.class);

                    toSessionInNewGroup.putExtra(getResources().getString(R.string.moiming_user_data_key), curMoimingUser);
                    toSessionInNewGroup.putExtra(getResources().getString(R.string.session_creation_with_new_group_flag), true);

                    startActivity(toSessionInNewGroup);

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

        receiveIntent();

        prepareKakaoFriends();

        initView();

        initParams();


        try {

            FCMAppToken.getFcmAppAccessToken(this, fcmCallBack);

        } catch (IOException e) {

            Log.w("ACC_TOKEN", e.getMessage());

        }

        receiveNotification();


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

                // TODO: 내가 하는 애니메이션은 안됨.. ㅈ같음.. 이상한 검은 화면이 뜸 ㅅㅂ.. 도저히 왜안되는지 모르겠음.

            }
        });


        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toNotification = new Intent(MainActivity.this, NotificationActivity.class);

                toNotification.putExtra(getResources().getString(R.string.moiming_user_data_key), curMoimingUser);
                toNotification.putParcelableArrayListExtra(getResources().getString(R.string.search_group_data_key), homeFragment.getGroupAndMemberDataList());
                toNotification.putParcelableArrayListExtra(getResources().getString(R.string.moiming_notification_data_key), (ArrayList<? extends Parcelable>) rawNotificationList);

                startActivity(toNotification);

            }
        });

        mainNavigation.getMenu().

                getItem(1).

                setEnabled(false);

        mainNavigation.setOnNavigationItemSelectedListener(mainNavListener);
        mainNavigation.setSelectedItemId(R.id.btn_main_home);

    }

    private void receiveIntent() {

        Intent dataReceived = getIntent();
        curMoimingUser = (MoimingUserVO) dataReceived.getExtras().getSerializable("moiming_user");

    }

//    public void setCurUserGroupList(ArrayList<MoimingGroupVO> curUserGroupList) {
//
//        this.curUserGroupList = curUserGroupList;
//
//    }

    private void receiveNotification() {

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

                    }

                    @Override
                    public void onComplete() {

                        parseNotification();

                        // 나머지 아답터에 전달해서 아답터에서 해결!
                        if(IS_NOTIFICATION_REFRESH_NEEDED){ //  이거 중이라서 여기 온거면

                            homeFragment.getUgLinkers();

//                            IS_NOTIFICATION_REFRESH_NEEDED = false;

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

//        curUserGroupList = new ArrayList<>();

        // 변수 확인
        fragmentManager = getSupportFragmentManager();

        // FG 정의
        homeFragment = new MoimingHomeFragment();
        reserveFragment = new MoimingReserveFragment();

        rawNotificationList = new ArrayList<>();
        rawNotificationMap = new HashMap<>();

        /*//  FAB 에니메이션 정의
        mainFabOpen = new TranslateAnimation(0, 0, 0, 0);
        mainFabClose = new TranslateAnimation(0, 0, 0, 0);
        mainFabOpen.setDuration(300);
        mainFabOpen.setFillAfter(true);
        mainFabClose.setDuration(300);
        mainFabClose.setFillAfter(true);


        mainFabOpen.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                CoordinatorLayout.LayoutParams newBtn = (CoordinatorLayout.LayoutParams) btnDutchpayNew.getLayoutParams();
                CoordinatorLayout.LayoutParams oldBtn = (CoordinatorLayout.LayoutParams) btnDutchpayOld.getLayoutParams();

                newBtn.bottomMargin += openYDelta;
                oldBtn.bottomMargin += openYDelta;

                btnDutchpayNew.setLayoutParams(newBtn);
                btnDutchpayOld.setLayoutParams(oldBtn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });


        mainFabClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                CoordinatorLayout.LayoutParams newBtn = (CoordinatorLayout.LayoutParams) btnDutchpayNew.getLayoutParams();
                CoordinatorLayout.LayoutParams oldBtn = (CoordinatorLayout.LayoutParams) btnDutchpayOld.getLayoutParams();

                newBtn.bottomMargin += closeYDelta;
                oldBtn.bottomMargin += closeYDelta;

                btnDutchpayNew.setLayoutParams(newBtn);
                btnDutchpayOld.setLayoutParams(oldBtn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });*/

    }


    private void initView() {

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

//            btnDutchpayOld.startAnimation(mainFabOpen);
//            btnDutchpayNew.startAnimation(mainFabOpen);

            transparent_bg.setVisibility(View.GONE);

            btnDutchpayOld.setClickable(false);
            btnDutchpayOld.setVisibility(View.GONE);
            btnDutchpayNew.setClickable(false);
            btnDutchpayNew.setVisibility(View.GONE);


            isFabOpen = false;

        } else {

//            btnDutchpayOld.startAnimation(mainFabOpen);
//            btnDutchpayNew.startAnimation(mainFabClose);

            transparent_bg.setVisibility(View.VISIBLE);

            btnDutchpayOld.setClickable(true);
            btnDutchpayOld.setVisibility(View.VISIBLE);
            btnDutchpayNew.setClickable(true);
            btnDutchpayNew.setVisibility(View.VISIBLE);

            isFabOpen = true;
        }
    }

    // 다른 액티비티 후에 돌아올 경우 onRestart 가 실행된다.
    @Override
    protected void onRestart() {
        super.onRestart();

        if (IS_USER_UPDATED) { // Refresh 가 필요한 경우

            // User Info Update
            updateCurUser();

            IS_USER_UPDATED = false;

        }

        if(IS_NOTIFICATION_REFRESH_NEEDED){

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